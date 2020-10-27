$(function(){
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
});

function like(btn, entityType, entityId, entityUserId,postId){
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"postId":postId},
        function(data) {
            //把返回的字符串  转换为js对象
            data = $.parseJSON(data);
            if(data.code == 0) {
                //btn代表当前标签对象 我们可以获得他的下级标签<b>和<i>
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?'已赞':"赞");
            } else {
                alert(data.msg);
            }
        }
    );
}

// 置顶
function setTop(){
    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id":$("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                /*置顶成功后 需要把按钮设置为不可用*/
                $("#topBtn").attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        }
    );
}
// 加精
function setWonderful() {
    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {"id":$("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                $("#wonderfulBtn").attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        }
    );
}

// 删除
function setDelete() {
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"id":$("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                location.href = CONTEXT_PATH + "/index";
            } else {
                alert(data.msg);
            }
        }
    );
}