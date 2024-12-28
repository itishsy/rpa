import fitz  # PyMuPDF


# 因表格分页读取不了下一页第一行  现在需要将多页合并为一页  在进行表格读取
# margins 两页的边距  pages 总页数  output_pdf 输出路径  input_pdf输入文档路径
def merge_pdf_pages(input_pdf: str, output_pdf: str, pages: int, margins: int):
    if pages <= 0:
        return
        # 打开PDF文档
    pdf_document = fitz.open(input_pdf)
    page: int = 0
    # 所有的页
    allPages = []
    while page < pages:
        allPages.append(pdf_document.load_page(page))
        page += 1

    # 创建一个新的PDF文档用于存放合并后的页面
    merged_pdf = fitz.open()

    # 新建一个页面，该页面的高度为两个页面高度之和
    # 总高度 = 当页高度 * 所有页
    rect1 = allPages[0].rect
    width: float = rect1.width
    new_height: float = rect1.height * len(allPages)
    # 添加一个新页面
    new_page = merged_pdf.new_page(width=width, height=new_height)
    page = 0
    while page < pages:
        if 0 == page:
            # 将第一个页面内容绘制到新页面最前面
            new_page.show_pdf_page(rect1, pdf_document, 0)
        else:
            # 后续页面进行追加
            new_page.show_pdf_page(allPages[page].rect + (0, margins * page, 0, margins * page), pdf_document, page)
        page += 1
    # 保存合并后的文档
    merged_pdf.save(output_pdf)
    merged_pdf.close()
    pdf_document.close()

"""
def get_shenbaomingxin_detail(path: str):
    pdf = pdfplumber.open(path)
    record_total = 0
    pages: int = len(pdf.pages)
    if pages > 1:
        # 合并表格在读
        pdf.close()
        output_pdf = path[0:len(path) - 4] + "_merge.pdf"
        merge_pdf_pages(path, output_pdf, pages, 521)
        pdf = pdfplumber.open(output_pdf)
    i: int = 1
    shenbaominxi_count: int = 0
    shenbaominxi_data = []
    for page in pdf.pages:
        for pdf_table in page.extract_table():
            row = []
            for item in pdf_table:
                if item is not None:
                    row.append(item)
            text = row[0].replace("\n", "")
            if "序号" == text:
                continue
            if '合计' in text:
                break
            record_total = int(text)
            _i = str(i)
            # 加编号是为了防止后续取值精准
            metadata = {'No': i,
                        'monthStart_' + _i: row[6].replace("\n", ""),
                        'monthEnd_' + _i: row[7].replace("\n", ""),
                        'insurance_' + _i: row[3].replace("\n", ""),
                        'feeType_' + _i: row[4].replace("\n", ""),
                        'amt_' + _i: row[12].replace("\n", "").replace(",", "")
                        }
            shenbaominxi_data.append(metadata)
            i = i + 1
    shenbaominxi_count = record_total
    print(shenbaominxi_count)
    print(shenbaominxi_data)


# 示例调用
# merge_pdf_pages("D:/Desktop/123.pdf", "D:/Desktop/1231.pdf", 2, 521)
get_shenbaomingxin_detail('D:/Desktop/1231.pdf')
"""