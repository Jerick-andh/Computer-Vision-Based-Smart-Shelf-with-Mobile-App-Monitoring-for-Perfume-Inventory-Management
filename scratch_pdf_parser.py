import fitz
import sys

def extract_pdf_info(pdf_path):
    doc = fitz.open(pdf_path)
    
    with open('pdf_summary.txt', 'w', encoding='utf-8') as f:
        f.write("--- Introduction (Pages 8-10) ---\n")
        for i in range(7, 10):  # 0-indexed, so page 8 is index 7
            if i < doc.page_count:
                page = doc.load_page(i)
                f.write(page.get_text())
                
        f.write("\n--- Development (Pages 109-111) ---\n")
        for i in range(108, 111):
            if i < doc.page_count:
                page = doc.load_page(i)
                f.write(page.get_text())

if __name__ == '__main__':
    extract_pdf_info('capstone manuscript.pdf')
