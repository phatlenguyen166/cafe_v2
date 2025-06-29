document.addEventListener("DOMContentLoaded", function () {
  let selectedTable = null;
  let selectedTableName = "";
  let selectedTableStatus = null;
  const tableItems = document.querySelectorAll(".table-item");
  const openBookingModal = document.getElementById("openBookingModal");
  const bookingModal = document.getElementById("bookingModal");
  const closeBookingModal = document.getElementById("closeBookingModal");
  const modalTableName = document.getElementById("modalTableName");
  const bookingForm = document.getElementById("bookingForm");
  const tableIdInput = document.getElementById("tableIdInput");
  const reservationDateInput = document.getElementById("reservationDateInput");
  const dateInput = document.getElementById("dateInput");
  const timeInput = document.getElementById("timeInput");

  tableItems.forEach((item) => {
    item.addEventListener("click", function () {
      // Luôn cho chọn bàn
      const status = this.getAttribute("data-status");
      const isSelected = this.classList.contains("border-blue-500");
      if (isSelected) {
        this.classList.remove("border-blue-500");
        this.querySelector(".check-icon").classList.add("hidden");
        selectedTable = null;
        selectedTableName = "";
        selectedTableStatus = null;
        openBookingModal.disabled = true; // Disable nút đặt bàn khi bỏ chọn
        return;
      }
      tableItems.forEach((i) => {
        i.classList.remove("border-blue-500");
        i.querySelector(".check-icon").classList.add("hidden");
      });
      this.classList.add("border-blue-500");
      this.querySelector(".check-icon").classList.remove("hidden");
      selectedTable = this.getAttribute("data-id");
      selectedTableName = this.querySelector("span").innerText;
      selectedTableStatus = status;
      // Bật/tắt nút đặt bàn theo trạng thái
      openBookingModal.disabled = status !== "AVAILABLE";
    });
  });

  if (
    openBookingModal &&
    bookingModal &&
    closeBookingModal &&
    modalTableName &&
    bookingForm
  ) {
    openBookingModal.addEventListener("click", function () {
      if (!selectedTable || selectedTableStatus !== "AVAILABLE") {
        alert("Chỉ có thể đặt bàn với bàn đang trống!");
        return;
      }
      modalTableName.textContent = "Đặt bàn " + selectedTableName;
      tableIdInput.value = selectedTable;
      bookingModal.classList.remove("hidden");
    });

    closeBookingModal.addEventListener("click", function () {
      bookingModal.classList.add("hidden");
    });

    bookingForm.addEventListener("submit", function (e) {
      // Xóa lỗi cũ
      document.getElementById("customerNameError").textContent = "";
      document.getElementById("customerPhoneError").textContent = "";
      document.getElementById("dateError").textContent = "";
      document.getElementById("timeError").textContent = "";

      let hasError = false;

      // Validate tên khách hàng
      const customerName = document
        .getElementById("customerNameInput")
        .value.trim();
      if (!customerName) {
        document.getElementById("customerNameError").textContent =
          "Vui lòng nhập tên khách hàng!";
        hasError = true;
      }

      // Validate số điện thoại
      const customerPhone = document
        .getElementById("customerPhoneInput")
        .value.trim();
      if (!/^\d{9,11}$/.test(customerPhone)) {
        document.getElementById("customerPhoneError").textContent =
          "Số điện thoại không hợp lệ (9-11 số)!";
        hasError = true;
      }

      // Validate ngày
      const date = dateInput.value;
      if (!date) {
        document.getElementById("dateError").textContent =
          "Vui lòng chọn ngày!";
        hasError = true;
      }

      // Validate giờ
      const time = timeInput.value;
      if (!time) {
        document.getElementById("timeError").textContent = "Vui lòng chọn giờ!";
        hasError = true;
      }

      // Validate ngày giờ phải ở tương lai
      if (date && time) {
        const now = new Date();
        const reservationDate = new Date(`${date}T${time}`);
        if (reservationDate <= now) {
          document.getElementById("dateError").textContent =
            "Ngày giờ phải ở tương lai!";
          hasError = true;
        }
        reservationDateInput.value = `${date}T${time}`;
      } else {
        reservationDateInput.value = "";
      }

      if (hasError) {
        e.preventDefault();
        return false;
      }
      // Không preventDefault nếu hợp lệ, form sẽ submit bình thường
    });
  }
});
