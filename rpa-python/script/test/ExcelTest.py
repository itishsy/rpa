import xlrd

def start():
    wb = xlrd.open_workbook(r'D:\sh_jy.xls', encoding_override="GBK")

    sheet_names = wb.sheet_names()

    print(sheet_names)

    sheet = wb.sheet_by_name(sheet_names[0])

    print(sheet.nrows, sheet.ncols)

    for r in range(sheet.nrows):
        for c in range(sheet.ncols):
            va = sheet.cell(r, c).value
            print(va, end='\t')
    print()


start()
