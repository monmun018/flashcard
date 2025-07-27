<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
            <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
            <html>

            <head>
                <meta charset="UTF-8">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Học</title>
                <link rel="stylesheet" href="..\css\flashcard.css">
                <link rel="stylesheet" href="..\css\nav.css">
                <script src="..\JavaScript\script.js"></script>
            </head>

            <body>
                <!-- Header -->
                <div class="logo">
                    Flash Card On Web
                </div>
                <ul>
                    <li><a href="../home">Trang chủ</a></li>
                    <li><a href="../profile">Thông tin cá nhân</a></li>
                    <li><a href="../report">Báo cáo học tập</a></li>
                    <li><a href="../addDeck">Thêm bộ thẻ</a></li>
                    <li><a href="../addCard">Thêm thẻ</a></li>
                    <li style="float: right;"><a href="/logout">Đăng xuất</a></li>
                </ul>

                <!-- Card -->
                <div class="card">
                    <div class="flip-card-3D-wrapper">
                        <div id="flip-card">
                            <div class="flip-card-front">
                                <p>${font}</p>
                                <button id="flip-card-btn-turn-to-back">Lật</button>
                            </div>
                            <div class="flip-card-back">
                                <p>${back}</p>
                                <button id="flip-card-btn-turn-to-front">Lật</button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Answer -->
                <form method="post">
                    <div class="answer" method="POST">
                        <button class="button1" formaction="../answer/1"> Học lại </button>
                        <button class="button2" formaction="../answer/2"> Khó </button>
                        <button class="button3" formaction="../answer/3"> Được </button>
                        <button class="button4" formaction="../answer/4"> Tốt </button>
                    </div>
                </form>


            </body>

            </html>