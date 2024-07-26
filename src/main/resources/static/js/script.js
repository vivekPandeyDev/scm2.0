console.log("Script loaded");
const changeThemeButton = document.querySelector("#theme_change_button");
const alertMessage = document.getElementById("alert-message");
const htmlElement = document.querySelector("html");
const themeButtonSpanElement = document.querySelector("#theme_change_button").querySelector("span")
/*<![CDATA[*/
const LIGHT_THEME =  /*[[#{light_mode}]]*/ "Light";
const DARK_THEME = /*[[#{dark_mode}]]*/ "Dark";
/*]]>*/

console.log(themeButtonSpanElement)

document.addEventListener("DOMContentLoaded", () => {
  const currentTheme = getStorageTheme();
  applyCurrentTheme(currentTheme)
});

changeThemeButton.addEventListener("click", () => {
  const currentTheme = getStorageTheme();
  changeCurrentTheme(currentTheme)
});


//set theme to localstorage
function setStorageTheme(theme) {
  localStorage.setItem("theme", theme);
}

//get theme from localstorage
function getStorageTheme() {
  let theme = localStorage.getItem("theme");
  return theme || "light";
}


function changeCurrentTheme(currentTheme) {
  if (currentTheme === LIGHT_THEME) {
    htmlElement.classList.remove(LIGHT_THEME)
    htmlElement.classList.add(DARK_THEME)
    themeButtonSpanElement.textContent = DARK_THEME
    setStorageTheme(DARK_THEME)
  } else {
    htmlElement.classList.remove(DARK_THEME)
    htmlElement.classList.add(LIGHT_THEME)
    themeButtonSpanElement.textContent = LIGHT_THEME
    setStorageTheme(LIGHT_THEME)
  }
}

function applyCurrentTheme(currentTheme) {
  if (currentTheme === LIGHT_THEME) {
    htmlElement.classList.remove(DARK_THEME)
    htmlElement.classList.add(LIGHT_THEME)
    themeButtonSpanElement.textContent = LIGHT_THEME
  } else {
    htmlElement.classList.remove(LIGHT_THEME)
    htmlElement.classList.add(DARK_THEME)
    themeButtonSpanElement.textContent = DARK_THEME
  }
}

// observer

function hideElementAfterDelay(elementId, delay) {
  const element = document.getElementById(elementId);
  console.log("element" , element)
  if (element) {
    setTimeout(() => {
      element.style.transition = 'opacity 0.5s';  // Optional: for smooth fade out
      element.style.opacity = 0;
      setTimeout(() => {
        element.style.display = 'none';
      }, 500); // Matches the transition duration
    }, delay);
  }
}


// Observe changes in the DOM
const observer = new MutationObserver((mutations) => {
  mutations.forEach((mutation) => {
    // Directly check for the element existence in the addedNodes
    const addedNodes = Array.from(mutation.addedNodes);
    if (addedNodes.some(node => node.id === 'alert-message')) {
      hideElementAfterDelay('alert-message', 3000); // 3 seconds delay
    }
  });
});

// Start observing the targetNode for child nodes
observer.observe(document.body, { childList: true, subtree: true });


