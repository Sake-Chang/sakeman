$(".create-post").click(function() {                     // ボタンがクリックされたら
    $(this).toggleClass('active');                  // ボタン自身にactiveクラスを付与
    $("#g-nav").toggleClass('panelactive');         // ナビゲーションにpanelactiveクラスを付与
    $(".circle-bg").toggleClass('circleactive');    // 丸背景にcircleactivクラスを付与
});

$("#g-nav a").click(function() {
    $(".openbtn").removeClass('active');                  // ボタン自身にactiveクラスを付与
    $("#g-nav").removeClass('panelactive');         // ナビゲーションにpanelactiveクラスを付与
    $(".circle-bg").removeClass('circleactive');
});