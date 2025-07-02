document.addEventListener("DOMContentLoaded", function () {
  const addExpenseBtn = document.getElementById("addExpenseBtn");
  const fromDateInput = document.getElementById("fromDate");
  const toDateInput = document.getElementById("toDate");

  addExpenseBtn.addEventListener("click", function () {
    window.location.href = "/expenses/create";
    alert("Chức năng thêm chi tiêu sẽ được phát triển");
  });

  // Set min date cho date inputs
  fromDateInput.addEventListener("change", function () {
    toDateInput.min = this.value;
  });

  // Enter để submit form
  [fromDateInput, toDateInput].forEach((input) => {
    input.addEventListener("keypress", function (e) {
      if (e.key === "Enter") {
        input.form.submit();
      }
    });
  });

  // Format tiền tệ cho các phần tử có class 'money-value'
  document.querySelectorAll(".money-value").forEach(function (el) {
    let value = el.textContent.replace(/\D/g, ""); // Lấy số, bỏ ký tự khác
    if (value) {
      el.textContent = Number(value).toLocaleString("vi-VN") + " đ";
    } else {
      el.textContent = "0 đ";
    }
  });

  document.querySelectorAll(".date-value").forEach(function (el) {
    let value = el.textContent.trim();
    // Hỗ trợ cả yyyy-MM-dd và yyyy/MM/dd
    let match = value.match(/^(\d{4})[-\/](\d{1,2})[-\/](\d{1,2})$/);
    if (match) {
      let day = match[3].padStart(2, "0");
      let month = match[2].padStart(2, "0");
      let year = match[1];
      el.textContent = `${day}/${month}/${year}`;
    }
  });
});
