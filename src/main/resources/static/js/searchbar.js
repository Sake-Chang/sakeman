document.addEventListener('DOMContentLoaded', function() {
  const headerObserver = new IntersectionObserver(function(entries) {
    entries.forEach(function(entry) {
      if (entry.isIntersecting) {
        console.log('Header is visible');
        var searchIcon = document.getElementById('search-icon');
        var searchBarContainer = document.getElementById('search-bar-container');
        var searchBox = document.getElementById('search-box');

        searchIcon.addEventListener('click', function() {
          if (searchBarContainer.style.display === 'none') {
            searchBarContainer.style.display = 'block';
            searchBox.focus();
            if (searchBox.value) {
              searchBox.select();
            }
          } else {
            searchBarContainer.style.display = 'none';
          }
        });

        // 一度表示されたら監視を停止
        headerObserver.disconnect();
      }
    });
  });

  headerObserver.observe(document.querySelector('header'));
});