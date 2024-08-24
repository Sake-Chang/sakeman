document.addEventListener('DOMContentLoaded', function () {
  document.getElementById('search-icon').addEventListener('click', function () {
    var searchBarContainer = document.getElementById('search-bar-container');
    var searchBox = document.getElementById('search-box'); // 検索ボックスのIDを取得
    if (searchBarContainer.style.display === 'none') {
      searchBarContainer.style.display = 'block';
      searchBox.focus(); // 検索バーを表示した後にフォーカスを当てる
    } else {
      searchBarContainer.style.display = 'none';
    }
  });
});