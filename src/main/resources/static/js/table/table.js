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
  const cancelTableBtn = document.getElementById("cancelTableBtn");
  const cancelTableForm = document.getElementById("cancelTableForm");
  const cancelTableIdInput = document.getElementById("cancelTableIdInput");
  const openSwitchTableModal = document.getElementById("openSwitchTableModal");
  const switchTableModal = document.getElementById("switchTableModal");
  const closeSwitchTableModal = document.getElementById(
    "closeSwitchTableModal"
  );
  const switchFromTableName = document.getElementById("switchFromTableName");
  const fromTableIdInput = document.getElementById("fromTableIdInput");
  const openMenuModalBtn = document.getElementById("openMenuModal");
  const menuModal = document.getElementById("menuModal");
  const closeMenuModal = document.getElementById("closeMenuModal");
  const menuModalTableName = document.getElementById("menuModalTableName");
  const menuTableIdInput = document.getElementById("menuTableIdInput");
  const menuList = document.getElementById("menuList");
  const viewTableBtn = document.getElementById("viewTableBtn");
  const viewTableModal = document.getElementById("viewTableModal");
  const closeViewTableModalBtns = document.querySelectorAll(
    "#closeViewTableModal"
  );
  const viewTableModalTitle = document.getElementById("viewTableModalTitle");
  const viewTableMenuList = document.getElementById("viewTableMenuList");
  const viewTableReservationInfo = document.getElementById(
    "viewTableReservationInfo"
  );
  const viewTableForm = document.getElementById("viewTableForm");
  const viewTableIdInput = document.getElementById("viewTableIdInput");

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
        if (openSwitchTableModal) {
          openSwitchTableModal.disabled = true; // Disable nút chuyển bàn khi bỏ chọn
        }
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
      // Bật/tắt nút hủy bàn theo trạng thái
      if (cancelTableBtn) {
        cancelTableBtn.disabled = status !== "RESERVED";
      }
      // Bật/tắt nút chuyển bàn theo trạng thái
      if (openSwitchTableModal) {
        openSwitchTableModal.disabled = !(
          status === "OCCUPIED" || status === "RESERVED"
        );
      }
      // Bật/tắt nút xem bàn: chỉ cho xem nếu KHÔNG phải bàn trống
      if (viewTableBtn) {
        viewTableBtn.disabled = status === "AVAILABLE";
      }
      // Bật/tắt nút thanh toán: chỉ cho phép với bàn đang sử dụng
      const openPaymentModalBtn = document.getElementById("openPaymentModal");
      if (openPaymentModalBtn) {
        openPaymentModalBtn.disabled = status !== "OCCUPIED";
      }
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
        // Nếu time chỉ có HH:mm, thêm :00 cho giây
        let timeStr = time.length === 5 ? time + ":00" : time;
        // Nếu muốn thêm mili giây, có thể thêm ".000"
        reservationDateInput.value = `${date}T${timeStr}.000`; // hoặc `${date}T${timeStr}.000`
        // Kiểm tra hợp lệ
        const now = new Date();
        const reservationDate = new Date(`${date}T${timeStr}`);
        if (reservationDate <= now) {
          document.getElementById("dateError").textContent =
            "Ngày giờ phải ở tương lai!";
          hasError = true;
        }
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

  if (cancelTableBtn && cancelTableForm && cancelTableIdInput) {
    cancelTableBtn.addEventListener("click", function () {
      if (!selectedTable || selectedTableStatus !== "RESERVED") {
        alert("Chỉ có thể hủy với bàn đã đặt trước!");
        return;
      }
      if (!confirm("Bạn có chắc chắn muốn hủy đặt bàn này?")) {
        return;
      }
      cancelTableIdInput.value = selectedTable;
      cancelTableForm.submit();
    });
  }

  if (
    openSwitchTableModal &&
    switchTableModal &&
    closeSwitchTableModal &&
    switchFromTableName &&
    fromTableIdInput
  ) {
    openSwitchTableModal.addEventListener("click", function () {
      if (
        !selectedTable ||
        (selectedTableStatus !== "OCCUPIED" &&
          selectedTableStatus !== "RESERVED")
      ) {
        alert("Chỉ chuyển được bàn đã đặt hoặc đang sử dụng!");
        return;
      }
      switchFromTableName.textContent = selectedTableName;
      fromTableIdInput.value = selectedTable;
      // Reset select về mặc định
      document.getElementById("toTableIdInput").selectedIndex = 0;
      switchTableModal.classList.remove("hidden");
    });

    closeSwitchTableModal.addEventListener("click", function () {
      switchTableModal.classList.add("hidden");
    });
  }

  if (openMenuModalBtn && menuModal && closeMenuModal) {
    openMenuModalBtn.addEventListener("click", function () {
      if (!selectedTable) {
        alert("Vui lòng chọn bàn trước!");
        return;
      }
      menuModalTableName.textContent = "Chọn món bàn " + selectedTableName;
      menuTableIdInput.value = selectedTable;
      menuModal.classList.remove("hidden");
    });

    closeMenuModal.addEventListener("click", function () {
      menuModal.classList.add("hidden");
    });

    // Xử lý submit thực đơn
    const menuForm = document.getElementById("menuForm");
    menuForm.addEventListener("submit", function (e) {
      e.preventDefault();

      // Xóa các trường invoiceDetails cũ nếu có
      document
        .querySelectorAll(".dynamic-invoice-detail")
        .forEach((el) => el.remove());

      const menuCheckboxes = document.querySelectorAll(".menu-checkbox");
      let idx = 0;
      for (let i = 0; i < menuCheckboxes.length; i++) {
        if (menuCheckboxes[i].checked) {
          const index = menuCheckboxes[i].getAttribute("data-index");
          const menuId = document.querySelector(
            `.menu-id[data-index="${index}"]`
          ).value;
          const quantity = document.querySelector(
            `.menu-quantity[data-index="${index}"]`
          ).value;
          const price = document.querySelector(
            `.menu-price[data-index="${index}"]`
          ).value;

          if (parseInt(quantity) > 0) {
            // Tạo input động cho từng trường
            let menuItemIdInput = document.createElement("input");
            menuItemIdInput.type = "hidden";
            menuItemIdInput.name = `invoiceDetails[${idx}].menuItemId`;
            menuItemIdInput.value = menuId;
            menuItemIdInput.classList.add("dynamic-invoice-detail");
            menuForm.appendChild(menuItemIdInput);

            let quantityInput = document.createElement("input");
            quantityInput.type = "hidden";
            quantityInput.name = `invoiceDetails[${idx}].quantity`;
            quantityInput.value = quantity;
            quantityInput.classList.add("dynamic-invoice-detail");
            menuForm.appendChild(quantityInput);

            let priceInput = document.createElement("input");
            priceInput.type = "hidden";
            priceInput.name = `invoiceDetails[${idx}].price`;
            priceInput.value = price;
            priceInput.classList.add("dynamic-invoice-detail");
            menuForm.appendChild(priceInput);

            idx++;
          }
        }
      }

      if (idx === 0) {
        alert("Vui lòng chọn ít nhất một món và nhập số lượng > 0!");
        return;
      }

      // Submit lại form
      menuForm.submit();
    });
  }

  if (viewTableBtn && viewTableForm && viewTableIdInput) {
    viewTableBtn.addEventListener("click", function () {
      if (!selectedTable) {
        alert("Vui lòng chọn bàn trước!");
        return;
      }
      viewTableIdInput.value = selectedTable;
      viewTableForm.submit();
    });
  }

  // Tự động mở modal xem bàn nếu có biến selectedTableId (render từ backend)
  const selectedTableId = document.body.getAttribute("data-selected-table-id");
  if (selectedTableId && viewTableModal) {
    viewTableModal.classList.remove("hidden");
  }

  if (closeViewTableModalBtns && viewTableModal) {
    closeViewTableModalBtns.forEach((btn) => {
      btn.addEventListener("click", function () {
        window.location.href = "/sale"; // Redirect về trang danh sách bàn
        viewTableModal.classList.add("hidden");
      });
    });
  }

  // Mở modal thanh toán khi nhấn nút "Thanh toán"
  const openPaymentModalBtn = document.getElementById("openPaymentModal");
  const paymentModal = document.getElementById("paymentModal");
  const closePaymentModalBtn = document.getElementById("closePaymentModal");

  if (openPaymentModalBtn && paymentModal) {
    openPaymentModalBtn.addEventListener("click", function () {
      if (!selectedTable) {
        alert("Vui lòng chọn bàn trước!");
        return;
      }
      // TODO: Render dữ liệu món, tổng tiền, tên bàn vào modal nếu cần
      paymentModal.classList.remove("hidden");
    });
  }

  if (closePaymentModalBtn && paymentModal) {
    closePaymentModalBtn.addEventListener("click", function () {
      paymentModal.classList.add("hidden");
    });
  }

  if (
    paymentModal &&
    typeof selectedTableId !== "undefined" &&
    window.location.pathname.includes("/sale/payment")
  ) {
    paymentModal.classList.remove("hidden");
  }
});
