<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%> 
<%@ page contentType="text/html;charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>浦东新区档案馆档案信息管理系统</title>
<link type="text/css" rel="stylesheet" href="css/v2/basic.css" />

<script type="text/javascript" src="js/jquery-1.9.1.js" charset="UTF-8"></script>
<script src="js/jquery/jquery.cookies.2.2.0.min.js"></script>
<%
String errormsgs = (String)request.getAttribute("errormsgs") ;
if(errormsgs != null && !"".equals(errormsgs)){
	out.println("<script language=\"javascript\">alert('"+errormsgs+"');$.cookies.set( 'pd2.default.submenuid','0', {expiresAt: new Date( 2050, 1, 1 )});try{window.parent.document.getElementById('goodexit').value='true';}catch(ex){}; window.parent.location='/PD2/index.jsp'; </script>");
} 
%>

<style>
#login{position:relative;}
#username, #password, #paskey{border:0; background-color:#FFF;}
#username{position:absolute; top:112px; right:60px;}
#password{position:absolute; top:150px; right:60px;}
#paskey{position:absolute; top:202px; right:60px;}
.btnStyle01{position:absolute; top:230px; right:53px;}
</style>
<script type="text/javascript"> 
$(document).ready(function(){	
	 $("#btnLogin").click(function(){
	 	login1();
	 });
});
function login1()
{  
  var acount = $.trim( $("#username").val() ) ;
  var password = $.trim( $("#password").val() ) ;
  var paskey=$.trim($("#paskey").val());
  if(paskey=="")
  {
  	  if( acount == "" ) { alert('请输入用户名!'); return;  }
  	  if( password == "")  { alert('请输入密码!'); return;  }
	  if (password.length<8||!password.match(/[0-9]/)||!password.match(/[a-zA-Z]/))
	  {
	  	$.cookies.set('passworderror','密码不符合规则!密码规则为:密码长度必须大于8位小于16位,密码需要以数字和字母组成!请及时更正');
	  }
  }
  else
  {
    /* 初始化KEY */
    SafeEngineCtl.SEH_InitialSession(27,"com1",paskey, 0, 27, "com1", "");
	if(SafeEngineCtl.ErrorCode!=0)
	{
		alert("未插入USBKEY或密码错误！");
		SafeEngineCtl.SEH_ClearSession();
		return false;
	}
	   /* 获取客户端KEY中证书 */
	strCert = SafeEngineCtl.SEH_GetSelfCertificate(27, "com1", "");
	if(SafeEngineCtl.ErrorCode!=0)
	{
		alert("获取USBKEY内的证书失败,请重试！");
		SafeEngineCtl.SEH_ClearSession();
		return false;
	}

	/* 配置函数（0为不验证黑名单，建议选择“0”）*/
	i = SafeEngineCtl.SEH_SetConfiguration(0);
	if(SafeEngineCtl.ErrorCode!=0)
	{
		alert("配置失败,请重试！");
		SafeEngineCtl.SEH_ClearSession();
		return false;
	}	
	
    var match=generateMixed(8);
	/* 签名随机数 */
	strSigned = SafeEngineCtl.SEH_SignData(match, 3);
	if(SafeEngineCtl.ErrorCode!=0)
	{
		alert("签名失败,请重试！");
		SafeEngineCtl.SEH_ClearSession();
		return false;
	}
		/* 释放 */
	SafeEngineCtl.SEH_ClearSession();
	$("#cCert").val(strCert);
	$("#cSign").val(strSigned);
	$("#mAtch").val(match);
	$.cookies.set('paskey',paskey);
  }
  $("#loginForm").submit();
}
function login2()
{
  if ( event.keyCode == 13)  	
  	login1();
} 
var chars = ['0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'];
function generateMixed(n) {
     var res = "";
     for(var i = 0; i < n ; i ++) {
         var id = Math.ceil(Math.random()*35);
         res += chars[id];
     }
     return res;
}

//首页登录框回车按钮响应
function enterSubmit(){  
   var password = $.trim( $("#password").val() ) ;
   var paskey=$.trim($("#paskey").val());
   var event = arguments.callee.caller.arguments[0] || window.event;//消除浏览器差异  
   if (event.keyCode == 13){  
	   (password != "" && paskey != "") ? alert("请只选择一种登录方式！") : login1();
   }  
}  


// system 1D45FEED5B4781BD
</script> 
</head>

<body id="main">
<p align="center">
<object id="SafeEngineCtl" classid="CLSID:B48B9648-E9F0-48A3-90A5-8C588CE0898F" width="0" height="0" border=0 ></object>
</p>
<div id="login">
    <div style="width:535px; height:75px; margin:120px auto 0 auto; background:url(images/bg_head_login.png) no-repeat;"></div>
    <div style="width:535px; height:300px; margin:0 auto 0 auto; background:url(images/login-2.png) no-repeat; position:relative;">
	    <form id="loginForm" name="loginForm" method="post" action="logon">
	    	<input type="hidden" id="cCert" name="cCert" value=""/> 
			<input type="hidden" id="cSign" name="cSign" value=""/> 
			<input type="hidden" id="mAtch" name="mAtch" value=""/>
			<input id="username" class="input135" value="" name="account" type="text" />
			<input id="password" class="input135" value="" name="password" type="password" onkeydown="enterSubmit()" />
			<input id="paskey" class="input135" value="" name="paskey" type="password" onkeydown="enterSubmit()" />
	        <div class="btnStyle01" id="btnLogin"><a href="javascript:void(0);">确 定</a></div>
	     </form>
    </div>
    <div style="width:535px; margin:0 auto; padding-right:60px; color:#EEE; font-size:11px; text-align:right;">请使用IE8或以上版本浏览器。建议在1920×1080及以上分辨率下浏览</div>
</div>

</body>
</html>
