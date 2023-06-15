// アコーディオンをクリックした時の動作
$('.title').on('click', function() {
    var findElm = $(this).next(".box");
    $(findElm).slideToggle();
    if($(this).hasClass('close')){
        $(this).removeClass('close');
    } else {
        $(this).addClass('close');
    }
});