<%@ page language="java" contentType="text/html; charset=UTF-8"
 pageEncoding="UTF-8"%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
 <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
 "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" href="css\login.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
</head>
<body>
    <div class="background">
        <div class="shape"></div>
        <div class="shape"></div>
    </div>
    <form:form method="POST" action = "loginHandler" modelAttribute="loginFrom">
        <h3>Login</h3>
        <label for="loginID">Username</label>
        <form:input type="text" placeholder="Username" size="20" path="loginID"/>
        <form:errors path="loginID" />
        <label for="pw">Password</label>
        <form:input type="password" placeholder="Password" size="20" path="pw"/>
        <form:errors path="pw" />
        <button>Log In</button>
        <div class="social">
          <a href=""><div class="go"><i class="fab fa-google"></i>  Google</div></a>
          <a href="register"><div class="dk"> Đăng ký</div></a>
        </div>
    </form:form>
</body>
</html>
