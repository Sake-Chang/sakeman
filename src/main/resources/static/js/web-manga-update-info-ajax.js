$(document).ready(function() {
    // ページネーションリンクのクリックイベントを処理
    $(document).on('click', '.pagenation a', function(e) {
        e.preventDefault(); // デフォルトのページ遷移を無効化

        var url = $(this).attr('href'); // 押されたリンクのURL
        var newPage = $(this).data('page'); // ページ番号を取得

        // URLを変更してブラウザの履歴にページ番号を追加
        history.pushState({page: newPage}, '', `?page=${newPage}`);

        // Ajaxリクエストでコンテンツを取得し、ページを更新
        $.ajax({
            url: url,
            type: 'GET',
            success: function(data) {
                $('#web-manga-update-info-card-container').html(data); // コンテンツを更新
            },
            error: function(xhr) {
                console.error("Error: " + xhr.responseText); // エラー処理
            }
        });
    });

    // popstateイベントで戻るボタンや進むボタンの動作に対応
    window.addEventListener('popstate', function(event) {
        if (event.state && event.state.page !== null) {
            var page = event.state.page; // 履歴からページ番号を取得

            // Ajaxリクエストで履歴に応じたページを再度読み込む
            $.ajax({
                url: `?page=${page}`,
                type: 'GET',
                success: function(data) {
                    $('#web-manga-update-info-card-container').html(data); // コンテンツを更新
                },
                error: function(xhr) {
                    console.error("Error: " + xhr.responseText); // エラー処理
                }
            });
        }
    });
});


//let page = 0; // 現在のページ番号
//let loading = false; // データロード中かどうか
//
//$(window).scroll(function() {
//    // ページの下部に達したとき
//    if ($(window).scrollTop() + $(window).height() >= $(document).height() - 100 && !loading) {
//        loading = true; // ロード中フラグを立てる
//        page++; // ページ番号を増やす
//
//        $.ajax({
//            url: `/web-manga-update-info?page=${page}`, // データ取得用のURL
//            type: 'GET',
//            success: function(response) {
//                $('#card-container').append(response); // 取得したデータを追加
//                loading = false; // ロード中フラグを解除
//            },
//            error: function() {
//                console.error('データの取得に失敗しました。');
//                loading = false; // エラー時もフラグを解除
//            }
//        });
//    }
//});
