package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

//登陆进去后进行的个人设置 ：上传头像，修改密码等等
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;


    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;

    //跳转到个人设置的页面
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(Model model) {
        // 上传文件名称
        String fileName = CommunityUtil.generateUUID();
        StringMap policy = new StringMap();
        policy.put("returnBody", CommunityUtil.getJSONString(0));
        // 生成上传凭证
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);

        model.addAttribute("uploadToken", uploadToken);
        model.addAttribute("fileName", fileName);

        return "/site/setting";
    }

    // 更新头像路径
    @RequestMapping(path = "/header/url", method = RequestMethod.POST)
    @ResponseBody
    public String updateHeaderUrl(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return CommunityUtil.getJSONString(1, "文件名不能为空!");
        }

        String url = headerBucketUrl + "/" + fileName;
        userService.updateHeader(hostHolder.getUser().getId(), url);

        return CommunityUtil.getJSONString(0);
    }

    //废弃
    //上传头像  更换了数据库中的头像数据
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }
        //读取文件的后缀  原始文件名
        String fileName = headerImage.getOriginalFilename();
        //从最后一个小数点开始往后截取
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //若没有后缀名 则说文件的格式不正确
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error", "文件的格式不正确!");
            return "/site/setting";
        }   
        //生成随机的文件名  随机文件名
        fileName= CommunityUtil.generateUUID() + suffix;

        File file=new File(uploadPath);
        //确定文件存放的路径  新创建以恶个目录 将头像存放进去
        //File dest = new File(uploadPath + "/" + fileName);
        if(!file.exists()){
            file.mkdir();
        }
        try {
            //headerImage.transferTo(new File(uploadPath , fileName));
            headerImage.transferTo(new File(uploadPath + "/" + fileName));
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }
        //存储头像成功后  更新用户头像（web访问路径）
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl=domain+contextPath+"/user/header/"+fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    //废弃
    //获取用户的头像
    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        // 头像在 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/"+suffix);
        try(
                //try语句结束后 这个对象会自动进行close方法关闭
                //开启一个文件流  读取其中的文件
                FileInputStream fls=new FileInputStream(fileName);
                //获取返回的而输出流  以图片的形式返回
                OutputStream os=response.getOutputStream();
                ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while( (b = fls.read(buffer))!=-1){
                /**
                 * 写出文件  三个参数：
                 * buffer：从这个数组中读取数据
                 * off: 每次从零读取  因为第一次写入的数据被读取后数组会清空
                 * len: 每次都读取len个字节的字符
                 */
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    //修改密码
    @LoginRequired
    @RequestMapping(path = "/restPassword",method = RequestMethod.POST)
    public String restPassword(String oldPassword, String newPassword, String newPassword1,@CookieValue("ticket")String ticket, Model model){
        //原密码和新密码 都不能为空
        if(StringUtils.isBlank(oldPassword)){
            model.addAttribute("oldPasswordMsg","原密码不能为空！");
            return "/site/setting";
        }
        if(StringUtils.isBlank(newPassword)){
            model.addAttribute("newPasswordMsg","新密码不能为空！");
            return "/site/setting";
        }
        if(StringUtils.isBlank(newPassword1)){
            model.addAttribute("PasswordMsgConfirm","确认密码不能为空！");
            return "/site/setting";
        }

        //判断新密码是否和旧密码相同
        if(newPassword.equals(oldPassword)){
            model.addAttribute("newPasswordMsg","新密码不能与旧密码相同！");
            return "/site/setting";
        }
        //判断两次密码是否保持一致
        if(!newPassword.equals(newPassword1)){
            model.addAttribute("PasswordMsgConfirm","两次密码不一致！");
            return "/site/setting";
        }
        //判断原密码是否正确  先获取当前的用户信息
        User user = hostHolder.getUser();
        //数据库中保存的密码都是经过盐值加密过的数据  输入的旧密码也需要先加密在比较
        oldPassword=CommunityUtil.md5(oldPassword+user.getSalt());
        if(!user.getPassword().equals(oldPassword)){
            model.addAttribute("oldPasswordMsg","原密码输入错误！");
            return "/site/setting";
        }else{
            //修改密码  新密码需要经过MD5的加密加上盐值才行
            newPassword=CommunityUtil.md5(newPassword+user.getSalt());
            //更新密码
            userService.updatePassword(user.getId(),newPassword);
            //修改密码后重新登陆
            userService.logout(ticket);
            return "/site/login";
        }
    }



    //个人主页  点赞显示   根据用户头像可以查看用户信息 用户获得的赞的数量  这里的userId是那个页面的ID
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {

        User loginUser= hostHolder.getUser();
        model.addAttribute("loginUser", loginUser);
        User user = userService.selectById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        // 用户
        model.addAttribute("user", user);
        // 点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        //关注的数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // 是否已关注
        boolean hasFollowed =false;
        if(hostHolder.getUser()!=null){
            hasFollowed =  followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);
        return "/site/profile";
    }


}
