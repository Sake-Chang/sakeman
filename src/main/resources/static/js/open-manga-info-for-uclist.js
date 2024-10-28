document.addEventListener('DOMContentLoaded', () => {
    // チェックボックスの状態が変わると発動
    document.body.addEventListener('change', (event) => {
        if (event.target.matches('.manga-info-toggle')) {
            const checkbox = event.target;
            const mangaId = checkbox.id.split('-').pop(); // ユニークID取得

            // モーダル要素を取得
            const overlay = document.getElementById(`overlay-${mangaId}`);
            const content = document.getElementById(`modal-content-${mangaId}`);
            const dropdown = checkbox.closest('.manga-info-dropdown');

            if (checkbox.checked) {
                // モーダルとオーバーレイを body 直下に移動し表示
                document.body.appendChild(overlay);
                document.body.appendChild(content);
                overlay.style.display = 'block';
                content.style.display = 'block';
            } else {
                // 非表示にして元の位置に戻す
                overlay.style.display = 'none';
                content.style.display = 'none';
                dropdown.appendChild(overlay);
                dropdown.appendChild(content);
            }
        }
    });

    // 閉じるボタンまたはオーバーレイでモーダルを閉じる処理
    document.body.addEventListener('click', (event) => {
        if (event.target.matches('.modal-close') || event.target.matches('.modal-overlay')) {
            const mangaId = event.target.getAttribute('for')
                ? event.target.getAttribute('for').split('-').pop()
                : event.target.id.split('-').pop(); // manga IDを取得
            const checkbox = document.getElementById(`manga-info-toggle-${mangaId}`);
            const overlay = document.getElementById(`overlay-${mangaId}`);
            const content = document.getElementById(`modal-content-${mangaId}`);
            const dropdown = checkbox.closest('.manga-info-dropdown');

            // チェックを外し、元の位置に戻す
            checkbox.checked = false;
            overlay.style.display = 'none';
            content.style.display = 'none';
            dropdown.appendChild(overlay);
            dropdown.appendChild(content);
        }
    });
});
