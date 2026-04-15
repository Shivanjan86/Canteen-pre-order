from pathlib import Path
import textwrap
from fpdf import FPDF

root = Path(__file__).resolve().parent
md_path = root / "PROJECT_DOCUMENTATION.md"
pdf_path = root / "Canteen_Preorder_System_Documentation.pdf"

text = md_path.read_text(encoding="utf-8")

pdf = FPDF()
pdf.set_auto_page_break(auto=True, margin=15)
pdf.add_page()
pdf.set_font("Helvetica", size=11)


def write_wrapped(line: str, prefix: str = ""):
    normalized = line.replace("\t", "    ")
    wraps = textwrap.wrap(
        normalized,
        width=105,
        break_long_words=True,
        break_on_hyphens=False,
    )
    if not wraps:
        pdf.ln(4)
        return

    for i, segment in enumerate(wraps):
        if i == 0 and prefix:
            pdf.multi_cell(0, 6, f"{prefix}{segment}", new_x="LMARGIN", new_y="NEXT")
        else:
            pdf.multi_cell(0, 6, segment, new_x="LMARGIN", new_y="NEXT")

for raw_line in text.splitlines():
    line = raw_line.rstrip()

    if not line:
        pdf.ln(4)
        continue

    if line.startswith("# "):
        pdf.set_font("Helvetica", style="B", size=16)
        pdf.multi_cell(0, 9, line[2:], new_x="LMARGIN", new_y="NEXT")
        pdf.ln(2)
        pdf.set_font("Helvetica", size=11)
        continue

    if line.startswith("## "):
        pdf.set_font("Helvetica", style="B", size=13)
        pdf.multi_cell(0, 8, line[3:], new_x="LMARGIN", new_y="NEXT")
        pdf.ln(1)
        pdf.set_font("Helvetica", size=11)
        continue

    if line.startswith("### "):
        pdf.set_font("Helvetica", style="B", size=12)
        pdf.multi_cell(0, 7, line[4:], new_x="LMARGIN", new_y="NEXT")
        pdf.set_font("Helvetica", size=11)
        continue

    if line.startswith("- "):
        write_wrapped(line[2:], prefix="* ")
        continue

    write_wrapped(line)

pdf.output(str(pdf_path))
print(f"Generated: {pdf_path}")
