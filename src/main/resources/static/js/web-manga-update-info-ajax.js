$(function() {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
    $(document).ready(function() {
        // ページ遷移
        $(document).on('click', '.pagenation a', function(e) {
            $('#loading-indicator').show();
            e.preventDefault();

            var url = $(this).attr('href'); // 押されたリンクのURL
            var newPage = $(this).data('page'); // ページ番号を取得

            history.pushState({page: newPage}, '', `?page=${newPage}`);

            $.ajax({
                url: url,
                type: 'GET',
                success: function(data) {
                    $('#web-manga-update-info-card-container').html(data);
                    window.scrollTo(0, 0);
                },
                error: function(xhr) {
                    console.error("Error: " + xhr.responseText);
                },
                complete: function() {
                    $('#loading-indicator').hide();
                }
            });
        });

        // popstateイベントで戻るボタンや進むボタンの動作に対応（ページ遷移の場合）
        window.addEventListener('popstate', function(event) {
            if (event.state && event.state.page !== null) {
                $('#loading-indicator').show();
                var page = event.state.page;

                $.ajax({
                    url: `?page=${page}`,
                    type: 'GET',
                    success: function(data) {
                        $('#web-manga-update-info-card-container').html(data);
                        window.scrollTo(0, 0);
                    },
                    error: function(xhr) {
                        console.error("Error: " + xhr.responseText);
                    },
                    complete: function() {
                        $('#loading-indicator').hide();
                    }
                });
            }
        });

        // フォームでの更新
        $(document).on('submit', '#web-manga-filter-form', function(e) {
            $('#loading-indicator').show();
            e.preventDefault();

            var form = $(this);
            var url = form.attr('action');
            var formData = form.serialize();
            console.log(formData)

            $.ajax({
                url: url,
                type: 'POST',
                data: formData,
                success: function() {
                    $('#webmanga-settings-popup').prop('checked', false);
                    updateMySetButtonStyle(formData);
                    $.ajax({
                        url: '/web-manga-update-info',
                        type: 'GET',
                        success: function(data) {
                            $('#web-manga-update-info-card-container').html(data);

                            $('input.webmanga-genres-checkbox-inform').each(function() {
                                var genreId = $(this).val();
                                var isChecked = $(this).is(':checked');

                                $('input.webmanga-genres-checkbox-outer').each(function() {
                                    if ($(this).val() === genreId) {
                                        $(this).prop('checked', isChecked);
                                    }
                                });
                            });
                        },
                        error: function(xhr) {
                            console.error("Error during GET: " + xhr.responseText);
                        }
                    });
                },
                error: function(xhr) {
                    console.error("Error during POST: " + xhr.responseText);
                },
                complete: function() {
                    $('#loading-indicator').hide();
                }
            });
        });


        // ページロード時や部分更新後にボタンのデザインを更新
        function updateMySetButtonStyle(formData) {
            var isDefaultSetting = isDefault(formData);

            if (isDefaultSetting) {
                $('.webmanga-setting-popup-open').removeClass('webmanga-settings-modified');
                $('.webmanga-setting-popup-open .material-icons-outlined').text('tune');
            } else {
                $('.webmanga-setting-popup-open').addClass('webmanga-settings-modified');
                $('.webmanga-setting-popup-open .material-icons-outlined').text('check');
            }
        }

        // フィルター設定がデフォルトかどうかを判定する関数
        function isDefault(formData) {
            var params = new URLSearchParams(formData);
            var freeflag = params.get('freeflag');
            var followflag = params.get('followflag');
            var oneshotflag = params.get('oneshotflag');
            var genres = params.getAll('genres');

            // デフォルトの状態と比較
            if (freeflag == 0 && followflag == 0 && oneshotflag == 0 && genres.length == 0) {
                return true;
            }
            return false;
        }

        // フォーム外での更新
        $('.webmanga-genres-checkbox-outer').on('change', function() {
            $('#loading-indicator').show();
            var isAnonymous = $(this).data('anonymous');
            if (isAnonymous) {
                return;
            }
            var selectedGenres = [];

            // 選択されているジャンルをすべて取得
            $('.webmanga-genres-checkbox-outer:checked').each(function() {
                selectedGenres.push($(this).val());
            });

            console.log("Selected genres:", selectedGenres);

            var freeflag = $('input[name="freeflag"]:checked').val();
            var followflag = $('input[name="followflag"]:checked').val();
            var oneshotflag = $('input[name="oneshotflag"]:checked').val();

            $.ajax({
                url: '/web-manga-update-info',
                type: 'POST',
                data: {
                    genres: selectedGenres.length > 0 ? selectedGenres.join(',') : '',
                    freeflag: freeflag,
                    followflag: followflag,
                    oneshotflag: oneshotflag
                },
                success: function() {
                    $.ajax({
                        url: '/web-manga-update-info',
                        type: 'GET',
                        success: function(data) {
                            $('#web-manga-update-info-card-container').html(data);
                            console.log("GET request success:", data);

                            // フォーム内のチェックボックスの状態を更新
                            selectedGenres.forEach(function(genreId) {
                                $('input.webmanga-genres-checkbox-inform').each(function() {
                                    if ($(this).val() === genreId) {
                                        $(this).prop('checked', true); // フォーム内のチェックボックスをチェックする
                                    }
                                });
                            });

                            // すべてのジャンルを一度確認し、選択されていないものを同期して外す
                            $('input.webmanga-genres-checkbox-inform').each(function() {
                                if (!selectedGenres.includes($(this).val())) {
                                    $(this).prop('checked', false); // 選択されていないものはチェックを外す
                                }
                            });

                        },
                        error: function(xhr) {
                            console.error("Error during GET: " + xhr.responseText);
                        }
                    });
                },
                error: function(xhr) {
                    console.error("Error during POST: " + xhr.responseText);
                },
                complete: function() {
                    $('#loading-indicator').hide();
                }
            });
        });

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