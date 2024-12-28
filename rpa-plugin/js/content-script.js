// 输入-事件监听器
document.addEventListener('input', onKeyboardInput, true);

// 移入-事件监听器
document.addEventListener("mouseover", onMouseOver, true);

// 移出-事件监听器
document.addEventListener("mouseout", onMouseOut, true);

// 点击-事件监听器
document.addEventListener("click", onMouseClick, true);

let isDownCtrl = false;
let ctrlCode = 17; //Ctrl按键码
let timer = null
let include_attr = ["name", "class", "placeholder", "title"];
let exclude_link = ["localhost", "rpa.seebon.com", "192.168.0."];

//shift按下
document.addEventListener("keydown", sendKeyCodeDown, true);

//shift抬起
document.addEventListener("keyup", sendKeyCodeUp, true);

function sendKeyCodeDown(event) {
    let e = event || window.event || arguments.callee.caller.arguments[0];
    if (e && e.keyCode == ctrlCode) {
        isDownCtrl = true;
    }
}

function sendKeyCodeUp(event) {
    let e = event || window.event || arguments.callee.caller.arguments[0];
    if (e && e.keyCode == ctrlCode) {
        isDownCtrl = false;
    }
}

// 输入-事件处理函数
function onKeyboardInput(event) {
    // 获取鼠标移入的元素
    const element = event.target;
    // 高亮元素
    element.style.outline = "";
    element.style.cursor = "";
}

// 移入-事件处理函数
function onMouseOver(event) {
    clearTimeout(timer)
    timer = setTimeout(() => {
        // 获取鼠标移入的元素
        const element = event.target;

        //是否排除
        let isExclude = isExcludeFun();
        if (isExclude) {
            return;
        }

        // 高亮元素
        element.style.outline = "2px solid red";
        //element.style.cursor = "pointer";

        // 生成XPath
        const xpath = readXPath(element);
        console.log("xpath=" + xpath)
        createSpan(xpath, event)
    }, 100)
}

function createSpan(text, event) {
    var span = document.createElement('span')
    span.style.color = 'red'
    span.id = '_tips'
    span.classList = '__tips'
    span.textContent = text
    span.style.position = 'absolute'
    span.style.border = '2px solid red'
    span.style.left = event.clientX + 'px'
    span.style.top = event.pageY + 'px'
    span.style.background = '#fff'
    span.style.zIndex = '99999999999999999'
    document.body.append(span)
}

// 移出-事件处理函数
function onMouseOut(event) {
    // 获取鼠标移出的元素
    const element = event.target;
    // 取消高亮元素
    element.style.outline = "";
    element.style.cursor = "";
    element.title = "";
    let id = document.querySelectorAll('.__tips')
    id.forEach(item => {
        document.body.removeChild(item)
    })
}

// 点击-事件处理函数
function onMouseClick(event) {
    // 获取点击的元素
    const element = event.target;

    //是否排除
    let isExclude = isExcludeFun();
    if (isExclude) {
        return;
    }

    // 取消高亮
    element.style.outline = "2px solid red";
    element.style.cursor = "";

    if (isDownCtrl) {
        // 生成XPath
        const xpath = readXPath(element);
        console.log("xpath=" + xpath)

        //复制
        copyXPath(xpath)

        // 阻止事件冒泡
        event.stopPropagation();
    }
}

function readXPath(element) {
    //匹配当前路径
    let currPath = getXPath(element, "");
    if (currPath.isOne) {
        return currPath.xpath;
    }
    //匹配父路径
    let parentPath = getParentPath(element, currPath.xpath);
    if (parentPath != null && parentPath.isOne) {
        return parentPath.xpath;
    }
}

//获取属性父元素唯一元素路径
function getParentPath(element, childPath) {
    let parentEl = element.parentElement;
    if (parentEl) {
        let result = getXPath(parentEl, childPath);
        if (result.isOne) {
            return result;
        } else {
            return getParentPath(parentEl, result.xpath)
        }
    }
    return null;
}

function getXPath(element, childPath) {
    let dataMap = {};
    dataMap.isOne = false;

    // 获取元素标签名
    var tagName = element.tagName.toLowerCase();
    console.log("tagName=" + tagName)

    // 生成初始的XPath表达式，包括标签名和部分属性
    let xpath = `//${tagName}`;

    //1、先检查元素是有Id属性
    if (element.id !== "") {
        dataMap.isOne = true;
        dataMap.xpath = '//' + tagName + '[@id="' + element.id + '"]';
        if (childPath && childPath.length > 0) {
            dataMap.xpath = '//' + tagName + '[@id="' + element.id + '"]' + childPath;
        }
        return dataMap;
    }

    //2、检查元素的其他属性的值是否唯一
    let attrPathMap = getAttrPath(element, childPath);
    if (attrPathMap.isOne) {
        dataMap.isOne = true;
        dataMap.xpath = attrPathMap.xpath;
        return dataMap;
    }

    //3、检查元素的文本是否全局唯一
    let textPathMap = getTextPath(element, childPath);
    if (textPathMap.isOne) {
        dataMap.isOne = true;
        dataMap.xpath = textPathMap.xpath;
        return dataMap;
    }

    //元素位置
    let index = getSiblingNum(element);
    dataMap.xpath = xpath + "[" + index + "]";
    if (childPath && childPath.length > 0) {
        dataMap.xpath = xpath + "[" + index + "]" + childPath;
    }
    return dataMap;
}

function getAttrPath(element, childPath) {
    // 获取元素标签名
    var tagName = element.tagName.toLowerCase();

    // 生成初始的XPath表达式，包括标签名和部分属性
    let xpath = `//${tagName}`;

    let dataMap = {};
    dataMap.isOne = false;
    dataMap.xpath = xpath;
    if (childPath && childPath.length > 0) {
        let index = getSiblingNum(element);
        dataMap.xpath = xpath + '[' + index + ']' + childPath;
    }

    //获取文档中标签的所有属性和值
    var attrValueMap = getAttrValueMap(tagName);

    // 获取元素所有属性
    var attributes = element.attributes;

    let attrPath = "";
    for (var i = 0; i < attributes.length; i++) {
        let attr = attributes[i];
        let name = attr.name;
        let value = attr.value;

        if (inAttr(name, value)) {
            continue;
        }

        //属性值是否唯一
        let isOnly = checkAttrVal(attrValueMap, name, value);
        if (!isOnly) {
            continue;
        }

        //是否已有唯一属性
        let flag = false;

        //class单独处理
        if (name == "class") {
            let classPath = "[contains(@class, '{}')]";
            let classList = value.split(" ");
            for (let j = 0; j < classList.length; j++) {
                let classItem = classList[j].trim();
                if (!classItem) {
                    continue;
                }
                let classIsOnly = checkAttrVal(attrValueMap, name, classItem);
                if (!classIsOnly) {
                    continue;
                }
                attrPath = classPath.replace("{}", classItem);
                flag = true;
                break;
            }
        }

        //class没有唯一值
        if (!flag) {
            attrPath = `[@${name}="${value}"]`;
        }

        // 根据属性名和属性值生成XPath表达式的一部分
        xpath = xpath + attrPath;
        if (childPath && childPath.length > 0) {
            xpath = xpath + childPath;
        }
        // 检查XPath是否唯一定位到目标元素
        var isFindOne = xpathIsFindOne(xpath);
        if (isFindOne) {
            dataMap.isOne = true;
            dataMap.xpath = xpath;
            return dataMap;
        }
    }
    return dataMap;
}

function getTextPath(element, childPath) {
    // 获取元素标签名
    var tagName = element.tagName.toLowerCase();

    // 生成初始的XPath表达式，包括标签名和部分属性
    let xpath = `//${tagName}`;

    let dataMap = new Map();
    dataMap.isOne = false;
    dataMap.xpath = xpath;
    if (childPath && childPath.length > 0) {
        let index = getSiblingNum(element);
        dataMap.xpath = xpath + '[' + index + ']' + childPath;
    }

    let text = element.innerText;
    //console.log("text=", text)

    let isOnlyText = checkTagText(tagName, text);
    console.log("isOnlyText=", isOnlyText)
    if (isOnlyText) {
        // 根据属性名和属性值生成XPath表达式的一部分
        xpath = xpath + "[contains(text(),'" + text + "')]";
        if (childPath && childPath.length > 0) {
            xpath = xpath + childPath;
        }

        // 检查XPath是否唯一定位到目标元素
        var isFindOne = xpathIsFindOne(xpath);
        if (isFindOne) {
            dataMap.isOne = true;
            dataMap.xpath = xpath;
            return dataMap;
        }
    }
    return dataMap;
}

//获取兄弟节点属性唯一元素路径
function getSiblingNum(element) {
    let index = 1, siblings = element.parentNode.childNodes;
    for (let i = 0, l = siblings.length; i < l; i++) {
        let sibling = siblings[i];
        if (sibling == element) {
            return index;
        } else if (sibling.nodeType == 1 && sibling.tagName == element.tagName) {
            index++;
        }
    }
    return index;
}

function xpathIsFindOne(xpath) {
    // 检查XPath是否唯一定位到目标元素
    var elements = document.evaluate(xpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
    if (elements.snapshotLength === 1) {
        return true;
    }
    return false;
}

//获取document的某个标签的所有属性和值
function getAttrValueMap(tagName) {
    let elements = document.getElementsByTagName(tagName)
    if (elements == null || elements.length == 0) {
        return false;
    }
    let dataMap = new Map();
    for (var i = 0; i < elements.length; i++) {
        var attributes = elements[i].attributes;
        for (var j = 0; j < attributes.length; j++) {
            var attr = attributes[j];
            var name = attr.name;
            var value = attr.value;
            if (inAttr(name, value)) {
                continue
            }
            let values = dataMap.get(name);
            if (values == null) {
                let valueList = [];
                if (name == "class") {
                    let classList = value.split(" ");
                    for (let k = 0; k < classList.length; k++) {
                        if (classList[k].trim()) {
                            valueList.push(classList[k].trim());
                        }
                    }
                } else {
                    valueList.push(value);
                }
                dataMap.set(name, valueList);
            } else {
                if (name == "class") {
                    let classList = value.split(" ");
                    for (let k = 0; k < classList.length; k++) {
                        if (classList[k].trim()) {
                            values.push(classList[k].trim());
                        }
                    }
                } else {
                    values.push(value);
                }
                dataMap.set(name, values);
            }
        }
    }
    return dataMap;
}

//判断某个属性值是否在整个document里唯一
function checkAttrVal(dataMap, attrName, attrValue) {
    let values = dataMap.get(attrName)
    let valueCount = 0;
    for (var i = 0; i < values.length; i++) {
        if (values[i] == attrValue) {
            valueCount = valueCount + 1;
        }
    }
    console.log("valueCount=", valueCount)
    if (valueCount == 1) {
        return true;
    }
    return false;
}

//获取document的某个标签的所有文本
function checkTagText(tagName, text) {
    if (checkText(text)) {
        return false;
    }
    let elements = document.getElementsByTagName(tagName)
    if (elements == null || elements.length == 0) {
        return false;
    }
    let textList = [];
    for (var i = 0; i < elements.length; i++) {
        var text = elements[i].text;
        if (checkText(text)) {
            continue;
        }
        textList.push(text);
    }

    let textCount = 0;
    for (var i = 0; i < textList.length; i++) {
        if (textList[i].includes(text)) {
            textCount = textCount + 1;
        }
    }
    console.log("textCount=", textCount)
    if (textCount == 1 || textCount == 0) {
        return true;
    }
    return false;
}

function copyXPath(xpath) {
    if (navigator.clipboard) { // 如果浏览器兼容该 API
        console.log("clipboard copy XPath=", xpath)
        try {
            navigator.clipboard.writeText(xpath);
            console.log("xpath =" + xpath + "已成功复制到剪贴板！");
        } catch (error) {
            console.error("复制文本到剪贴板失败：", error);
        }
    } else {
        console.log("iframe copy XPath=", xpath)

        let copyIframe = document.createElement("iframe");
        copyIframe.id = "xpathCopyIframe";
        copyIframe.style.height = "0px";
        copyIframe.style.width = "0px";
        copyIframe.style.display = "none"
        let input = document.createElement("textarea");
        input.id = "xpathCopyInput";
        input.style.height = "0px";
        input.style.width = "0px";
        document.body.appendChild(copyIframe);

        let iframeBody = document.querySelector("#xpathCopyIframe").contentDocument.body;
        iframeBody.appendChild(input);

        let iframeDocument = document.querySelector("#xpathCopyIframe").contentDocument;
        input = iframeDocument.querySelector("#xpathCopyInput");
        input.value = xpath;
        input.select();
        iframeDocument.execCommand('copy', false, null)
        document.body.removeChild(copyIframe);
    }
}

function isExcludeFun() {
    //当前链接
    let href = location.href;
    //是否排除
    let isExclude = false;
    for (var i = 0; i < exclude_link.length; i++) {
        if (href.includes(exclude_link[i])) {
            isExclude = true;
        }
    }
    return isExclude;
}

//是可以用属性
function inAttr(name, value) {
    if (name == "id" || value == "" || value.trim().length <= 0 || include_attr.indexOf(name) < 0) {
        return true;
    }
    return false;
}

//检查文本
function checkText(text) {
    if (text == "" || text == undefined || text.trim().length <= 0 || text.trim().length > 15) {
        return true;
    }
    return false;
}