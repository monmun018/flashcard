<%@ page language="java" contentType="text/html; charset=UTF-8"
 pageEncoding="UTF-8"%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
 <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
 "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Đăng ký</title>

    <link rel="stylesheet" href="css\register.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
</head>
<body>
    <div class="background">
        <div class="shape"></div>
        <div class="shape"></div>
    </div>
    <form:form method="POST" action = "register" modelAttribute="registFrom">
        <h3>Register</h3>
        <p>${mess}</p>
        <label for="loginID">Username</label>
        <form:input type="text" placeholder="Username" path="loginID" size="10" />

        <label for="pw">Password</label>
        <form:input type="password" placeholder="Password" path="pw" size="20" />

        <label for="name">Tên</label>
        <form:input type="text" placeholder="Tên" path="name" size="20" />

        <label for="age">Tuổi</label>
        <form:input type="text" placeholder="Tuổi" path="age" size="20" />

        <label for="mail">E-mail</label>
        <form:input type="text" placeholder="mail" path="mail" size="30" />

        <button>Đăng ký</button>
        <div class="social">
          <a href="/login"><div class="back-to-login"> Quay lại đăng nhập</div></a>
        </div>
    </form:form>
</body>
</html>
