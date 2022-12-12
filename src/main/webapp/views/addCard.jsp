<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
      <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
      <html>

      <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thêm thẻ mới</title>
        <link rel="stylesheet" href="css\nav.css">
        <link rel="stylesheet" href="css\form.css">
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
        <form:form method="POST" action="addCard" modelAttribute="cardForm">
          <label for="deckID">Nội dung mặt trước</label>
          <form:select  items="${listOption}" path="deckID" />
          <label for="fontContent">Nội dung mặt trước</label>
          <form:textarea placeholder="Hello" path="fontContent" />
          <!-- <form:input type="text" placeholder="Hello" path="fontContent" size="200" /> -->
          <label for="backContent">Nội dung mặt sau</label>
          <form:textarea placeholder="Xin chào!" path="backContent" />
          <!-- <form:input type="text" placeholder="Xin chào!" path="backContent" size="200" /> -->
          <div style="color: red;">
            <form:errors path="*" />
          </div>
          <button>Tạo</button>
        </form:form>
      </body>

      </html>