document.addEventListener("DOMContentLoaded", function () {
  let ingredients = [
    { name: "Cà phê", amount: 15, unit: "gam" },
    { name: "Sữa đặc", amount: 30, unit: "ml" },
    { name: "Nước nóng", amount: 150, unit: "ml" },
  ];
  let menuId = null;

  // Lấy các elements
  const ingredientsList = document.getElementById("ingredientsList");
  const addIngredientBtn = document.getElementById("addIngredientBtn");
  const menuForm = document.getElementById("menuForm");
  const cancelBtn = document.getElementById("cancelBtn");
  const loadingIndicator = document.getElementById("loadingIndicator");
  const errorMessage = document.getElementById("errorMessage");
  const retryBtn = document.getElementById("retryBtn");

  // Lấy menu ID từ URL
  function getMenuIdFromUrl() {
    const urlParts = window.location.pathname.split("/");
    return urlParts[urlParts.length - 1];
  }

  // Tải thông tin món ăn
  function loadMenuData() {
    menuId = getMenuIdFromUrl();

    if (!menuId || menuId === "edit") {
      showError();
      return;
    }

    showLoading();

    fetch(`/api/menus/${menuId}`)
      .then((response) => {
        if (!response.ok) {
          throw new Error("Menu không tồn tại");
        }
        return response.json();
      })
      .then((data) => {
        populateForm(data);
        hideLoading();
        showForm();
      })
      .catch((error) => {
        console.error("Error loading menu:", error);
        hideLoading();
        showError();
      });
  }

  // Điền dữ liệu vào form
  function populateForm(menuData) {
    document.getElementById("menuId").value = menuData.id;
    document.getElementById("tenMon").value = menuData.tenMon || "";
    document.getElementById("giaTien").value = menuData.giaTien || "";

    ingredients = menuData.thanhPhan || [];
    renderIngredients();
  }

  // Hiển thị/ẩn các phần tử
  function showLoading() {
    loadingIndicator.style.display = "block";
    menuForm.style.display = "none";
    errorMessage.style.display = "none";
  }

  function hideLoading() {
    loadingIndicator.style.display = "none";
  }

  function showForm() {
    menuForm.style.display = "block";
    errorMessage.style.display = "none";
  }

  function showError() {
    loadingIndicator.style.display = "none";
    menuForm.style.display = "none";
    errorMessage.style.display = "block";
  }

  // Thêm thành phần
  addIngredientBtn.addEventListener("click", function () {
    const name = document.getElementById("newIngredientName").value.trim();
    const amount = document.getElementById("newIngredientAmount").value.trim();
    const unit = document.getElementById("newIngredientUnit").value;

    if (name && amount && unit) {
      // Kiểm tra trùng lặp
      const existingIngredient = ingredients.find(
        (ing) => ing.name.toLowerCase() === name.toLowerCase()
      );
      if (existingIngredient) {
        alert("Thành phần này đã được thêm!");
        return;
      }

      const ingredient = {
        name: name,
        amount: parseFloat(amount),
        unit: unit,
      };

      ingredients.push(ingredient);
      renderIngredients();
      clearIngredientInputs();
    }
  });

  // Render danh sách thành phần
  function renderIngredients() {
    if (ingredients.length === 0) {
      ingredientsList.innerHTML = `
                <div class="grid grid-cols-3 py-8 text-center text-gray-500">
                    <div class="col-span-3">Chưa có thành phần nào</div>
                </div>
            `;
      return;
    }

    const html = ingredients
      .map(
        (ingredient, index) => `
            <div class="grid grid-cols-3 border-b border-gray-300 last:border-b-0">
                <div class="px-3 py-2 border-r border-gray-300">${ingredient.name}</div>
                <div class="px-3 py-2 border-r border-gray-300">${ingredient.amount}</div>
                <div class="px-3 py-2 flex justify-between items-center">
                    <span>${ingredient.unit}</span>
                    <button 
                        type="button" 
                        onclick="removeIngredient(${index})"
                        class="text-red-500 hover:text-red-700 ml-2"
                        title="Xóa thành phần"
                    >
                        ✕
                    </button>
                </div>
            </div>
        `
      )
      .join("");

    ingredientsList.innerHTML = html;
  }

  // Xóa thành phần
  window.removeIngredient = function (index) {
    if (confirm("Bạn có chắc muốn xóa thành phần này?")) {
      ingredients.splice(index, 1);
      renderIngredients();
    }
  };

  // Xóa input thành phần
  function clearIngredientInputs() {
    document.getElementById("newIngredientName").value = "";
    document.getElementById("newIngredientAmount").value = "";
    document.getElementById("newIngredientUnit").value = "";
  }

  // Submit form
  menuForm.addEventListener("submit", function (e) {
    e.preventDefault();

    const tenMon = document.getElementById("tenMon").value.trim();
    const giaTien = document.getElementById("giaTien").value.trim();
    const id = document.getElementById("menuId").value;

    if (!tenMon || !giaTien) {
      alert("Vui lòng điền đầy đủ tên món và giá tiền!");
      return;
    }

    const menuData = {
      id: id,
      tenMon: tenMon,
      giaTien: parseFloat(giaTien),
      thanhPhan: ingredients,
    };

    console.log("Dữ liệu cập nhật món ăn:", menuData);
    alert("Cập nhật món thành công! (Demo)");
  });

  // Hủy
  cancelBtn.addEventListener("click", function () {
    if (confirm("Bạn có chắc muốn hủy? Những thay đổi sẽ không được lưu.")) {
      // window.location.href = '/menus';
      console.log("Đã hủy chỉnh sửa");
    }
  });

  // Enter để thêm thành phần
  ["newIngredientName", "newIngredientAmount", "newIngredientUnit"].forEach(
    (id) => {
      document.getElementById(id).addEventListener("keypress", function (e) {
        if (e.key === "Enter") {
          e.preventDefault();
          addIngredientBtn.click();
        }
      });
    }
  );

  // Khởi tạo - render dữ liệu có sẵn
  renderIngredients();
});
