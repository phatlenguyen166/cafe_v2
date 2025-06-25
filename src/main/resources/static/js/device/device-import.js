// Giả lập danh sách thiết bị, sau này lấy từ BE
const equipmentList = [
  { id: 1, name: "Máy pha cà phê", purchasePrice: 5000000 },
  { id: 2, name: "Máy xay sinh tố", purchasePrice: 1500000 },
  { id: 3, name: "Tủ lạnh mini", purchasePrice: 3000000 },
];

function formatMoney(amount) {
  return amount.toLocaleString("vi-VN");
}

document.addEventListener("DOMContentLoaded", function () {
  // Render danh sách thiết bị
  const equipmentSelect = document.getElementById("equipmentSelect");
  equipmentList.forEach((eq) => {
    const option = document.createElement("option");
    option.value = eq.id;
    option.textContent = eq.name;
    option.dataset.price = eq.purchasePrice;
    equipmentSelect.appendChild(option);
  });

  // Set ngày hiện tại cho input date
  const importDate = document.getElementById("importDate");
  const today = new Date().toISOString().split("T")[0];
  importDate.value = today;

  // Khi chọn thiết bị, tự động fill đơn giá
  equipmentSelect.addEventListener("change", function () {
    const selected = equipmentList.find((eq) => eq.id == this.value);
    document.getElementById("purchasePrice").value = selected
      ? formatMoney(selected.purchasePrice)
      : "";
    updateTotalPrice();
  });

  // Khi thay đổi số lượng, cập nhật tổng tiền
  document
    .getElementById("quantity")
    .addEventListener("input", updateTotalPrice);

  function updateTotalPrice() {
    const priceRaw =
      equipmentList.find((eq) => eq.id == equipmentSelect.value)
        ?.purchasePrice || 0;
    const qty = parseInt(document.getElementById("quantity").value) || 0;
    document.getElementById("totalPrice").value = formatMoney(priceRaw * qty);
  }

  // Khởi tạo tổng tiền lần đầu
  updateTotalPrice();

  // Xử lý submit (sau này sẽ gửi lên BE)
  document
    .getElementById("deviceImportForm")
    .addEventListener("submit", function (e) {
      e.preventDefault();
      alert("Đã nhập thiết bị (BE xử lý sau)");
    });
});
