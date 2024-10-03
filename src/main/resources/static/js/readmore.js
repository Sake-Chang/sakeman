'use strict'

document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll('.description-text-container').forEach(function (container) {
        const description = container.querySelector('.card-description-text');
        const readMore = container.querySelector('.read-more');

        // 説明文が3行以上あるか判定
        if (description.scrollHeight <= description.clientHeight) {
            readMore.style.display = 'none'; // 3行以内なら「続きを読む」ボタンを非表示
        } else {
            readMore.style.display = 'inline'; // 3行以上なら「続きを読む」ボタンを表示
        }

        // 「続きを読む」クリックで全文表示
        readMore.addEventListener('click', function () {
            description.style.webkitLineClamp = 'unset'; // 行制限解除
            description.style.overflow = 'visible';
            readMore.style.display = 'none'; // ボタンを非表示
        });
    });
});


