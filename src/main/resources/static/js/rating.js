//$(document).ready(function() {
//    $("input[name='rating']").click(function() {
//        let $parentContainer = $(this).closest('.rating-wrapper');
//        let val = $("input[name='rating']:checked").val();
//        console.log(val);
//        $parentContainer.find("span").removeClass("md-star");
//        $(this).next('label').children('span').toggleClass("md-star");
//        $(this).prevAll('label').children('span').toggleClass("md-star");
//    });
//
//    $('.form-button').prop('disabled', true);
//
//    // select2のchangeイベントに応じてボタンを有効化/無効化
//    $('.select2-manga').change(function(){
//        let val2 = $('.select2-manga option:selected').val();
//        let isAnonymous = $('.form-button').data('is-anonymous');
//        console.log("選択された値:", val2);
//        console.log("ログインしていないか:", isAnonymous);
//
//        // 要素が選択されていて、かつログインしている場合にボタンを有効化
//        if (val2 && !isAnonymous) {
//            $('.form-button').prop('disabled', false);
//        } else {
//            $('.form-button').prop('disabled', true);
//        }
//
//        if (val2) {
//            const userId = document.querySelector("meta[name='user-id']").content;
//            const reviewForm = document.querySelector(".post-review-form-container");
//            console.log("userId:", userId);
//            console.log("mangaId:", val2);
//
//            fetch(`/review/check?mangaId=${val2}&userId=${userId}`)
//                .then(response => {
//                    if (!response.ok) {
//                        throw new Error("Failed to fetch review data.");
//                    }
//                    return response.json();
//                })
//                .then(data => {
//                    if (data.exists) {
//                        console.log("データはあるようです")
//                        // フォームに既存のレビュー内容を埋め込む
//                        reviewForm.querySelector("input[name='id']").value = data.id || "";
//                        reviewForm.querySelector("input[name='rating'][value='" + data.rating + "']").checked = true;
//                        reviewForm.querySelector("input[name='title']").value = data.title || "";
//                        reviewForm.querySelector("textarea[name='content']").value = data.content || "";
//
//                        // 星を塗る処理
//                        const ratingWrapper = reviewForm.querySelector(".rating-wrapper");
//                        const stars = ratingWrapper.querySelectorAll("span.material-icons-outlined");
//
//                        // すべての星をリセット
//                        stars.forEach(star => {
//                            star.classList.remove("md-star"); // 塗られた状態を解除
//                            star.classList.add("md-AS"); // デフォルトのクラスを追加
//                        });
//
//                        // 塗り直し
//                        stars.forEach((star, index) => {
//                            if (index < data.rating) {
//                                star.classList.add("md-star"); // 塗るクラスを追加
//                                star.classList.remove("md-AS"); // デフォルトを解除
//                            }
//                        });
//                    } else {
//                        // レビューが存在しない場合はフォームをリセット
//                        reviewForm.querySelector("input[name='id']").value = "";
//                        reviewForm.querySelectorAll("input[name='rating']").forEach(radio => (radio.checked = false));
//                        reviewForm.querySelector("input[name='title']").value = "";
//                        reviewForm.querySelector("textarea[name='content']").value = "";
//
//                        // 星をリセット
//                        const ratingWrapper = reviewForm.querySelector(".rating-wrapper");
//                        const stars = ratingWrapper.querySelectorAll("span.material-icons-outlined");
//                        stars.forEach(star => {
//                            star.classList.remove("md-star");
//                            star.classList.add("md-AS");
//                        });
//                    }
//                })
//                .catch(error => {
//                    console.error("Error fetching review data:", error);
//                });
//        } else {
//            console.log("きてないです", isAnonymous);
//        }
//
//    });
//  });

$(document).ready(function() {
    const submitButton = $('.form-button');

    function isFormValid() {
        const mangaSelected = !!$('.select2-manga option:selected').val();
        const ratingSelected = $("input[name='rating']:checked").length > 0;
        const isAnonymous = submitButton.data('is-anonymous');
//        console.log("漫画が選択されてる:" + mangaSelected)
//        console.log("星がチェックされてる:" + ratingSelected)
//        console.log("ログインされてる:" + !isAnonymous)
        return mangaSelected && ratingSelected && !isAnonymous;
    }

    function updateSubmitButtonState() {
        if (isFormValid()) {
            submitButton.prop('disabled', false);
        } else {
            submitButton.prop('disabled', true);
        }
    }

    function updateStars(rating) {
        const ratingWrapper = $(".rating-wrapper");
        const stars = ratingWrapper.find("span.material-icons-outlined");
        stars.each(function(index) {
            if (index < rating) {
                $(this).addClass("md-star").removeClass("md-AS");
            } else {
                $(this).removeClass("md-star").addClass("md-AS");
            }
        });
    }

    function initializeForm() {
        const initialRating = $("input[name='rating']:checked").val();
        if (initialRating) {
            updateStars(initialRating);
        }
        updateSubmitButtonState();
    }

    $("input[name='rating']").click(function() {
        const selectedRating = $(this).val();
        updateStars(selectedRating);
        updateSubmitButtonState();
    });

    $('.select2-manga').change(function() {
        const selectedMangaId = $('.select2-manga option:selected').val();
        const userId = document.querySelector("meta[name='user-id']").content;

        updateSubmitButtonState();

        if (selectedMangaId) {
            fetch(`/review/check?mangaId=${selectedMangaId}&userId=${userId}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Failed to fetch review data.");
                    }
                    return response.json();
                })
                .then(data => {
                    const reviewForm = document.querySelector(".post-review-form-container");
                    if (data.exists) {
                        reviewForm.querySelector("input[name='id']").value = data.id || "";
                        reviewForm.querySelector("input[name='rating'][value='" + data.rating + "']").checked = true;
                        reviewForm.querySelector("input[name='title']").value = data.title || "";
                        reviewForm.querySelector("textarea[name='content']").value = data.content || "";
                        updateStars(data.rating);
                    } else {
                        console.log("レビューが存在しません。フォームをリセットします。");
                        reviewForm.querySelector("input[name='id']").value = "";
                        reviewForm.querySelectorAll("input[name='rating']").forEach(radio => (radio.checked = false));
                        reviewForm.querySelector("input[name='title']").value = "";
                        reviewForm.querySelector("textarea[name='content']").value = "";
                        updateStars(0);
                    }
                    updateSubmitButtonState();
                })
                .catch(error => {
                    console.error("レビュー情報の取得に失敗しました:", error);
                });
        }
    });
    initializeForm();
});


document.addEventListener("DOMContentLoaded", function() {
    const currentUId = Number(document.querySelector("meta[name='current-UID']").content);
    const ownerUId = Number(document.querySelector("meta[name='owner-UID']").content);

    if (currentUId !== ownerUId) {
        document.querySelectorAll(".shelf-rating-wrapper").forEach(function (wrapper) {
            wrapper.style.pointerEvents = "none";
        });
        return;
    }

    document.querySelectorAll(".shelf-rating-wrapper input[type='radio']").forEach(function(radio) {
        radio.addEventListener("change", function(event) {
            const wrapper = event.target.closest(".shelf-rating-wrapper");
            const mangaId = Number(wrapper.dataset.mangaId);
            const rating = Number(event.target.value);

            fetch("/updateRating", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": document.querySelector("meta[name='_csrf']").content // CSRFトークン
                },
                body: JSON.stringify({
                    mangaId: mangaId,
                    rating: rating
                })
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Failed to update rating");
                }
                return response.json();
            })
            .then(data => {
                console.log("Rating updated successfully:", data);
                wrapper.querySelectorAll("input[type='radio']").forEach(function (radio) {
                    const label = radio.nextElementSibling;
                    const span = label.querySelector("span");
                    if (radio.value <= rating) {
                        span.classList.add("md-star");
                        span.classList.remove("md-AS");
                    } else {
                        span.classList.remove("md-star");
                        span.classList.add("md-AS");
                    }
                });
                alert("お気に入り度を更新しました！");
            })
            .catch(error => {
                console.error("Error updating rating:", error);
                alert("評価の更新に失敗しました。再試行してください。");
            });
        });
    });
});
