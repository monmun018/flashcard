<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
      <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
      <html>

      <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Danh sách bộ thẻ</title>
        <link rel="stylesheet" href="css\nav.css">
        <link rel="stylesheet" href="css\table.css">
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
        <table>
          <caption>Trang chủ</caption>
          <thead>
            <tr>
              <th scope="col">Tên bộ thẻ</th>
              <th scope="col">Chưa thuộc</th>
              <th scope="col">Đang học</th>
              <th scope="col">Đã thuộc</th>
              <th scope="col">Tổng số thẻ</th>
              <th scope="col">Thao tác</th>
            </tr>
          </thead>
          <tfoot>
            <tr>
              <td colspan="3">Bộ thẻ của ${userName}.</td>
            </tr>
          </tfoot>
          <tbody>
            <c:forEach var="deck" items="${decks}">
              <tr>
                <th scope="row"><a href="deck/${deck.getDeckID()}">${deck.getDeckName()}</a></th>
                <td>${deck.getNewCardNum()}</td>
                <td>${deck.getLearningCardNum()}</td>
                <td>${deck.getDueCardNum()}</td>
                <td>${deck.getNewCardNum()+deck.getLearningCardNum()+deck.getDueCardNum()}</td>
                <td><a href="deleteDeck/${deck.getDeckID()}">Xóa</a></td>
              </tr>
            </c:forEach>
          </tbody>
        </table>

      </body>

      </html>