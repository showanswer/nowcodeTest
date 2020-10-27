$(function(){
	/*标签选择器  表单提交的时候执行check_data函数*/
	$("form").submit(check_data);
	$("input").focus(clear_error);
});

//两次密码的判断机制   和加错误信息
function check_data() {
	var pwd1 = $("#password").val();
	var pwd2 = $("#confirm-password").val();
	if(pwd1 != pwd2) {
		$("#confirm-password").addClass("is-invalid");
		return false;
	}
	return true;
}
//当出现错误信息的那个输入框被选中 焦距的时候 移除class属性is-invalid
function clear_error() {
	$(this).removeClass("is-invalid");
}