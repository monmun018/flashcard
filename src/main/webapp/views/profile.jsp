<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
      <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
      <html>

      <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thông tin cá nhân</title>
        <link rel="stylesheet" href="css\nav.css">
        <link rel="stylesheet" href="css\form.css">
        <style>
          input[type=password] {
            width: 100%;
            padding: 12px 20px;
            margin: 8px 0;
            display: inline-block;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
          }
          .center{
            text-align: center;
          }
        </style>
      </head>

      <body>
        <div class="logo">
          Flash Card On Web
        </div>
        <ul>
          <li><a href="./home">Trang chủ</a></li>
          <li><a href="/profile">Thông tin cá nhân</a></li>
          <li><a href="/report">Báo cáo học tập</a></li>
          <li><a href="/addDeck">Thêm bộ thẻ</a></li>
          <li><a href="/addCard">Thêm thẻ</a></li>
          <li style="float: right;"><a href="/logout">Đăng xuất</a></li>
        </ul>
        <form:form method="POST" action="profile" modelAttribute="registFrom">
          <h3 class="center">Thông tin cá nhân</h3>
          <p>${mess}</p>
          <label for="loginID">Tên đăng nhập</label>
          <form:input type="text" placeholder="Tên đăng nhập" path="loginID" size="10" readonly="true" />

          <label for="pw">Mật khẩu</label>
          <form:input type="password" placeholder="Mật khẩu" path="pw" size="20" />

          <label for="name">Tên</label>
          <form:input type="text" placeholder="Tên" path="name" size="20" />

          <label for="age">Tuổi</label>
          <form:input type="text" placeholder="Tuổi" path="age" size="20" />

          <label for="mail">E-mail</label>
          <form:input type="text" placeholder="mail" path="mail" size="30" />

          <button>Thay đổi</button>
        </form:form>
      </body>

      </html>