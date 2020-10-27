$(function(){
    $("#uploadForm").submit(upload);
});

function upload() {
    $.ajax({
        //上传到客户端华北地址上
        url: "http://upload-z1.qiniup.com",
        method: "post",
        /*不把表单的内容转为字符串  上传的是文件*/
        processData: false,
        /*不让jquery 设置上传类型   浏览器自动设置 上传的是文件*/
        contentType: false,
        data: new FormData($("#uploadForm")[0]),
        success: function(data) {
            if(data && data.code == 0) {
                // 更新头像访问路径
                $.post(
                    CONTEXT_PATH + "/user/header/url",
                    {"fileName":$("input[name='key']").val()},
                    function(data) {
                        data = $.parseJSON(data);
                        if(data.code == 0) {
                            window.location.reload();
                        } else {
                            alert(data.msg);
                        }
                    }
                );
            } else {
                alert("上传失败!");
            }
        }
    });
    return false;
}