$(document).ready(function() {
    $("input[name='rating']").click(function() {
        let val = $("input[name='rating']:checked").val();
        console.log(val);
        $("span").removeClass("md-star");
        $("input[name='rating']:checked").next('label').children('span').toggleClass("md-star");
        $("input[name='rating']:checked").prevAll('label').children('span').toggleClass("md-star");
    });

    $('.form-button').prop('disabled', true);

    // select2のchangeイベントに応じてボタンを有効化/無効化
    $('.select2-manga').change(function(){
        let val2 = $('.select2-manga option:selected').val();
        let isAnonymous = $('.form-button').data('is-anonymous'); // サーバーから渡されたisAnonymousの状態を取得
        console.log("選択された値:", val2);
        console.log("ログインしていないか:", isAnonymous);

        // 要素が選択されていて、かつログインしている場合にボタンを有効化
        if (val2 && !isAnonymous) {
            $('.form-button').prop('disabled', false);
        } else {
            $('.form-button').prop('disabled', true);
        }
    });
  });