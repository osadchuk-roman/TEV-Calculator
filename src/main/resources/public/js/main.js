$("#btn-download").on("click", function () {
    let url = "/download?filename=" +document.getElementById("filename").textContent;
    console.log(url);
    $(location).attr('href', url);
});