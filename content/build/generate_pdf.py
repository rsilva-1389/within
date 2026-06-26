#!/usr/bin/env python3
"""
Within — 30-Day Journal: branded PDF generator.

Parses content/journal_30day_en.md (single source of truth) and renders a
premium, brand-styled PDF via WeasyPrint. Uses the app's own fonts (Fredoka /
Nunito), colors, and the Ori mascot.

Usage:
    /tmp/wjvenv/bin/python content/build/generate_pdf.py            # sample (cover + Phase 1)
    /tmp/wjvenv/bin/python content/build/generate_pdf.py --all      # full 30 days
"""
import re
import argparse
import pathlib
from weasyprint import HTML
from PIL import Image

REPO = pathlib.Path("/home/rsilva/projetos/within")
FONT = REPO / "app/src/main/res/font"
ASSETS_SRC = REPO / "app/src/main/assets"
BUILD = REPO / "content/build"
ASSET_OUT = BUILD / "assets"
MD = REPO / "content/journal_30day_en.md"
ASSET_OUT.mkdir(parents=True, exist_ok=True)

# ---------------------------------------------------------------- assets
def webp_to_png(rel, name):
    out = ASSET_OUT / name
    Image.open(ASSETS_SRC / rel).convert("RGBA").save(out)
    return out

ORI = webp_to_png("ori/expressions/ori_warmheart.webp", "ori_warmheart.png")

# ---------------------------------------------------------------- text helpers
def smart(s):
    """Straight quotes -> typographic quotes."""
    s = s.replace("'", "’")
    out, opening = [], True
    for ch in s:
        if ch == '"':
            out.append("“" if opening else "”")
            opening = not opening
        else:
            out.append(ch)
    return "".join(out)

def inline(s):
    s = re.sub(r"\*\*(.+?)\*\*", r"<strong>\1</strong>", s)
    s = re.sub(r"\*(.+?)\*", r"<em>\1</em>", s)
    return s

def fmt(s):
    return inline(smart(s.strip()))

def add_dropcap(html):
    """Wrap the first letter of the first paragraph in a drop-cap span."""
    m = re.match(r"(<p[^>]*>)(.)(.*)", html, re.S)
    if not m or not m.group(2).isalpha():
        return html
    return f'{m.group(1)}<span class="dropcap">{m.group(2)}</span>{m.group(3)}'

def render_blocks(chunk):
    """Render a slice of simple markdown (paragraphs, -lists, 1.lists, >quotes)."""
    html = []
    for b in re.split(r"\n\s*\n", chunk.strip()):
        lines = [ln.rstrip() for ln in b.split("\n") if ln.strip()]
        if not lines:
            continue
        if all(ln.lstrip().startswith("- ") for ln in lines):
            items = "".join(f"<li>{fmt(ln.lstrip()[2:])}</li>" for ln in lines)
            html.append(f"<ul>{items}</ul>")
        elif all(re.match(r"^\d+\.\s", ln.lstrip()) for ln in lines):
            items = "".join(
                f"<li>{fmt(re.sub(r'^\d+\.\s+', '', ln.lstrip()))}</li>" for ln in lines
            )
            html.append(f"<ol>{items}</ol>")
        elif all(ln.lstrip().startswith(">") for ln in lines):
            q = " ".join(ln.lstrip()[1:].strip() for ln in lines)
            html.append(f"<blockquote>{fmt(q)}</blockquote>")
        else:
            html.append(f"<p>{fmt(' '.join(lines))}</p>")
    return "\n".join(html)

# ---------------------------------------------------------------- parse markdown
md = MD.read_text()

day_re = re.compile(r"^### Day (\d+) · (.+?)\s*$", re.M)
matches = list(day_re.finditer(md))
DAYS, TITLES = {}, {}
for i, m in enumerate(matches):
    n = int(m.group(1))
    TITLES[n] = m.group(2).strip()
    start = m.end()
    end = matches[i + 1].start() if i + 1 < len(matches) else len(md)
    DAYS[n] = md[start:end]

LBL = [
    "**Theme:**", "**Reflection**", "**Journal Prompt**", "**Deep-Dive Questions**",
    "**Reaffirmation**", "**Mindful Action**", "**Evening Reflection**",
]

def parse_day(body):
    found = [(lab, body.find(lab)) for lab in LBL]
    secs = {}
    for lab, p in found:
        if p < 0:
            continue
        s = p + len(lab)
        later = [pp for _, pp in found if pp > p]
        if later:
            e = min(later)
        else:
            dash = body.find("\n---", s)
            e = dash if dash >= 0 else len(body)
        secs[lab] = body[s:e].strip()
    return secs

def day_html(n):
    s = parse_day(DAYS[n])
    theme = s["**Theme:**"]
    reflection = add_dropcap(render_blocks(s["**Reflection**"]))
    prompt = fmt(s["**Journal Prompt**"].lstrip("> ").strip())
    dd_items = "".join(
        f"<li>{fmt(re.sub(r'^\d+\.\s+', '', ln.strip()))}</li>"
        for ln in s["**Deep-Dive Questions**"].split("\n")
        if re.match(r"^\d+\.", ln.strip())
    )
    raw_aff = s["**Reaffirmation**"].lstrip("> ").strip().strip("*").strip().strip('"').strip()
    aff = f'<span class="qm">“</span>{fmt(raw_aff)}<span class="qm">”</span>'
    action = render_blocks(s["**Mindful Action**"])
    evening = fmt(s["**Evening Reflection**"].lstrip("> ").strip())
    return f"""<section class="day">
  <div class="day-kicker">Day {n:02d}</div>
  <h2 class="day-title">{smart(TITLES[n])}</h2>
  <div><span class="chip"><span class="k">Theme</span>{smart(theme)}</span></div>
  <div class="reflection">{reflection}</div>
  <div class="prompt-card">
    <div class="label">Journal Prompt</div>
    <p class="prompt-q">{prompt}</p>
    <div class="write-lines"><div class="ln"></div><div class="ln"></div><div class="ln"></div></div>
  </div>
  <div class="block deepdive">
    <div class="label">Deep-Dive Questions</div>
    <ol>{dd_items}</ol>
  </div>
  <div class="reaffirmation">
    <div class="label">Reaffirmation</div>
    <p class="affirm">{aff}</p>
  </div>
  <div class="block">
    <div class="label">Mindful Action <span class="time">· under 15 min</span></div>
    {action}
  </div>
  <div class="evening">
    <div class="label teal">Evening Reflection</div>
    <p class="evening-q">{evening}</p>
  </div>
</section>"""

# ---------------------------------------------------------------- front matter
PHASES = [
    ("Phase One", "Self-Acceptance", range(1, 8), "#FE856C"),
    ("Phase Two", "Empowerment", range(8, 16), "#3D8186"),
    ("Phase Three", "Connection", range(16, 24), "#FE856C"),
    ("Phase Four", "Purpose", range(24, 31), "#3D8186"),
]

def cover_html():
    return f"""<section class="cover">
  <div class="wordmark">WITHIN<span class="d">.</span></div>
  <div class="cover-main">
    <img class="ori" src="file://{ORI}">
    <h1 class="cover-title">The 30-Day<br>Journal</h1>
    <div class="cover-sub">Coming Home to Yourself</div>
    <hr class="cover-rule">
    <p class="cover-tag">A guided journey, in four movements,<br>from self-acceptance to purpose.</p>
  </div>
  <div class="cover-foot">Self-Acceptance<span class="sep">·</span>Empowerment<span class="sep">·</span>Connection<span class="sep">·</span>Purpose</div>
</section>"""

def preface_html():
    src = re.search(r"## Before You Begin\s*\n(.+?)\n---\n", md, re.S).group(1)
    inner = render_blocks(src).replace("<p>", '<p class="lead">', 1)
    # turn the closing "Take a breath…" paragraph into a centered send-off
    last = inner.rfind("<p>")
    if last != -1:
        inner = inner[:last] + '<p class="sendoff">' + inner[last + 3:]
    return (
        '<section class="front preface"><div class="eyebrow">A Note to Begin</div>'
        '<h2 class="section-title">Before You Begin</h2><hr class="rule-sm">'
        f"{inner}</section>"
    )

def toc_html():
    out = [
        '<section class="front toc"><div class="eyebrow">The Journey</div>'
        '<h2 class="section-title">30 Days, Four Movements</h2><hr class="rule-sm">'
    ]
    for kicker, name, rng, color in PHASES:
        items = "".join(
            f'<div class="toc-item"><span class="n">{n:02d}</span>{smart(TITLES[n])}</div>'
            for n in rng
        )
        out.append(
            f'<div class="toc-phase"><div class="toc-kicker" style="color:{color}">{kicker}</div>'
            f"<h3>{name}</h3><div class=\"toc-list\">{items}</div></div>"
        )
    out.append("</section>")
    return "".join(out)

def closing_html():
    m = re.search(r"## Where You Go From Here\s*\n(.+?)\s*$", md, re.S)
    if not m:
        return ""
    inner = render_blocks(m.group(1))
    last = inner.rfind("<p>")
    if last != -1:
        inner = inner[:last] + '<p class="sendoff-end">' + inner[last + 3:]
    return (
        '<section class="front closing"><div class="eyebrow">Closing</div>'
        '<h2 class="section-title">Where You Go From Here</h2><hr class="rule-sm">'
        f"{inner}</section>"
    )

def divider_html(kicker, name, rng):
    intro = re.search(
        rf"# {re.escape(kicker)} · {re.escape(name)}\s*\n\s*\*(.+?)\*\s*\n", md, re.S
    )
    intro_txt = fmt(intro.group(1)) if intro else ""
    lo, hi = rng.start, rng.stop - 1
    return f"""<section class="phase-divider" data-section="{kicker} · {name}">
  <div class="phase-eyebrow">{kicker}</div>
  <h1 class="phase-title">{name}</h1>
  <div class="phase-range">Days {lo} – {hi}</div>
  <hr class="phase-flourish">
  <p class="phase-intro">{intro_txt}</p>
</section>"""

# ---------------------------------------------------------------- CSS
def font_face(weight, fname, fam="Nunito"):
    return (
        f"@font-face{{font-family:'{fam}';font-weight:{weight};font-style:normal;"
        f"src:url('file://{FONT}/{fname}')}}"
    )

FONTS = "\n".join([
    font_face(400, "fredoka_regular.ttf", "Fredoka"),
    font_face(600, "fredoka_semibold.ttf", "Fredoka"),
    font_face(400, "nunito_regular.ttf"),
    font_face(600, "nunito_semibold.ttf"),
    font_face(700, "nunito_bold.ttf"),
    font_face(800, "nunito_extrabold.ttf"),
])

CSS = FONTS + r"""
@page {
  size: A5;
  margin: 16mm 15mm 15mm 15mm;
  background: #FBF3E7;
  @bottom-center { content: counter(page); font-family:'Nunito'; font-weight:700; font-size:8pt; color:#a99e88; }
  @bottom-left   { content: string(section); font-family:'Nunito'; font-weight:800; font-size:6.6pt; letter-spacing:.12em; text-transform:uppercase; color:#bfb59f; }
  @bottom-right  { content: 'within'; font-family:'Fredoka'; font-weight:600; font-size:7.5pt; letter-spacing:.06em; color:#cabfa8; }
}
@page cover { margin:0; @bottom-center{content:none} @bottom-left{content:none} @bottom-right{content:none} }
@page phase { @bottom-left{content:none} @bottom-right{content:none} }

* { box-sizing:border-box; }
html { background:#FBF3E7; }
body { margin:0; color:#001B39; font-family:'Nunito',sans-serif; font-weight:400; font-size:10pt; line-height:1.5; hyphens:none; }
p { margin:0 0 9px; }
strong { font-weight:800; }
em { font-style:italic; }

/* ---------------- COVER ---------------- */
.cover { page:cover; position:relative; overflow:hidden; width:148mm; height:210mm; background:#FBF3E7;
         padding:21mm 18mm 18mm; display:flex; flex-direction:column; justify-content:space-between; text-align:center; }
.cover::before { content:''; position:absolute; width:150mm; height:150mm; border-radius:50%; background:#FBE6DE; top:-88mm; right:-64mm; }
.cover::after  { content:''; position:absolute; width:122mm; height:122mm; border-radius:50%; background:#EDF2EC; bottom:-72mm; left:-56mm; }
.cover > * { position:relative; }
.wordmark { font-family:'Fredoka',sans-serif; font-weight:600; font-size:13pt; letter-spacing:.36em; color:#001B39; }
.wordmark .d { color:#FE856C; }
.cover-main { margin-top:-4mm; }
.cover .ori { width:33mm; height:auto; margin:0 auto 5mm; display:block; }
.cover-title { font-family:'Fredoka',sans-serif; font-weight:600; font-size:33pt; line-height:1.02; color:#001B39; margin:0 0 4mm; }
.cover-sub { font-family:'Nunito',sans-serif; font-weight:600; font-style:italic; font-size:13.5pt; color:#5A6B6E; margin-bottom:7mm; }
.cover-rule { width:42px; height:3px; background:#FE856C; border:0; border-radius:3px; margin:0 auto 7mm; }
.cover-tag { font-family:'Nunito',sans-serif; font-weight:400; font-size:10.5pt; color:#5A6B6E; line-height:1.55; }
.cover-foot { font-family:'Nunito',sans-serif; font-weight:800; font-size:7.4pt; letter-spacing:.15em; text-transform:uppercase; color:#7c7263; }
.cover-foot .sep { color:#FE856C; margin:0 5px; }

/* ---------------- FRONT MATTER ---------------- */
.front { break-before:page; }
.closing { string-set: section ""; }
.eyebrow { font-family:'Nunito',sans-serif; font-weight:800; font-size:8.2pt; letter-spacing:.2em; text-transform:uppercase; color:#FE856C; margin-bottom:2mm; }
.section-title { font-family:'Fredoka',sans-serif; font-weight:600; font-size:21pt; color:#001B39; margin:0 0 1mm; }
.rule-sm { width:36px; height:3px; background:#FE856C; border:0; border-radius:3px; margin:3mm 0 4mm; }
.front p { text-align:justify; margin:0 0 7px; hyphens:auto; }
.front .lead { font-size:10.5pt; }
.front ul { list-style:none; padding:0; margin:2.5mm 0; }
.front ul li { position:relative; padding-left:18px; margin:3.5px 0; line-height:1.45; text-align:left; }
.front ul li::before { content:''; position:absolute; left:0; top:6px; width:7px; height:7px; border-radius:50%; background:#FE856C; }
.front p.sendoff { break-before:page; text-align:center; font-family:'Fredoka',sans-serif; font-weight:600; font-size:13.5pt; line-height:1.5; color:#001B39; margin:74mm auto 0; max-width:78%; hyphens:none; }
.front p.sendoff::before { content:''; display:block; width:40px; height:3px; background:#FE856C; border-radius:3px; margin:0 auto 8mm; }
.front p.sendoff-end { text-align:center; font-family:'Fredoka',sans-serif; font-weight:600; font-size:12.5pt; line-height:1.45; color:#3D8186; margin:7mm auto 0; max-width:80%; hyphens:none; }
.front p.sendoff-end em { font-style:normal; }

/* ---------------- TOC ---------------- */
.toc-phase { margin:0 0 3mm; break-inside:avoid; }
.toc-kicker { font-family:'Nunito',sans-serif; font-weight:800; font-size:7.4pt; letter-spacing:.16em; text-transform:uppercase; }
.toc-phase h3 { font-family:'Fredoka',sans-serif; font-weight:600; font-size:12.5pt; color:#001B39; margin:0 0 1mm; }
.toc-list { display:flex; flex-wrap:wrap; margin-top:1mm; }
.toc-item { width:50%; font-size:8.7pt; line-height:1.42; color:#3a4a55; padding-right:6px; }
.toc-item .n { font-family:'Fredoka',sans-serif; font-weight:600; color:#FE856C; margin-right:5px; }

/* ---------------- PHASE DIVIDER ---------------- */
.phase-divider { page:phase; break-before:page; string-set: section attr(data-section); text-align:center; padding-top:56mm; }
.phase-eyebrow { font-family:'Nunito',sans-serif; font-weight:800; font-size:9pt; letter-spacing:.3em; text-transform:uppercase; color:#FE856C; margin-bottom:4mm; }
.phase-title { font-family:'Fredoka',sans-serif; font-weight:600; font-size:34pt; color:#001B39; margin:0; }
.phase-range { font-family:'Nunito',sans-serif; font-weight:700; font-size:10pt; letter-spacing:.1em; text-transform:uppercase; color:#9a8f7d; margin-top:3mm; }
.phase-flourish { width:46px; height:3px; background:#FE856C; border:0; border-radius:3px; margin:7mm auto; }
.phase-intro { font-family:'Nunito',sans-serif; font-weight:600; font-style:italic; font-size:11.5pt; line-height:1.62; color:#5A6B6E; max-width:80%; margin:0 auto; }

/* ---------------- DAY ---------------- */
.day { break-before:page; }
.day-kicker { font-family:'Nunito',sans-serif; font-weight:800; font-size:9pt; letter-spacing:.24em; text-transform:uppercase; color:#FE856C; }
.day-title { font-family:'Fredoka',sans-serif; font-weight:600; font-size:21pt; line-height:1.08; color:#001B39; margin:1mm 0 3mm; }
.chip { display:inline-block; background:#F1F4EF; border-radius:999px; padding:3px 12px 4px; font-family:'Nunito',sans-serif; font-weight:600; font-size:9.5pt; color:#001B39; }
.chip .k { font-weight:800; font-size:7.2pt; letter-spacing:.12em; text-transform:uppercase; color:#3D8186; margin-right:6px; }

.label { font-family:'Nunito',sans-serif; font-weight:800; font-size:8.2pt; letter-spacing:.16em; text-transform:uppercase; color:#FE856C; margin:0 0 4px; }
.label::before { content:''; display:inline-block; width:15px; height:2px; background:#FE856C; vertical-align:middle; margin-right:7px; margin-bottom:3px; }
.label.teal { color:#3D8186; }
.label.teal::before { background:#3D8186; }
.label .time { font-weight:600; color:#a99e88; letter-spacing:0; text-transform:none; font-size:7.6pt; }

.reflection { margin:4mm 0; }
.reflection p { text-align:justify; margin:0 0 7px; hyphens:auto; }
.reflection .dropcap { float:left; font-family:'Fredoka',sans-serif; font-weight:600; color:#FE856C; font-size:2.6em; line-height:1; margin:0.04em 0.16em 0 0; }

.prompt-card { background:#FFFFFF; border:1px solid #EFE3CF; border-left:3px solid #FE856C; border-radius:12px; padding:12px 15px 13px; margin:4mm 0; break-inside:avoid; }
.prompt-q { font-family:'Nunito',sans-serif; font-weight:700; font-size:11pt; line-height:1.4; color:#001B39; margin:0; }
.write-lines { margin-top:9px; }
.write-lines .ln { height:18px; border-bottom:1px solid #E8DCC6; }

.block { margin:4mm 0; break-inside:avoid; }
.deepdive ol { counter-reset:dd; list-style:none; padding:0; margin:0; }
.deepdive li { counter-increment:dd; position:relative; padding-left:25px; margin:4.5px 0; line-height:1.45; }
.deepdive li::before { content:counter(dd); position:absolute; left:0; top:-1px; font-family:'Fredoka',sans-serif; font-weight:600; font-size:11pt; color:#FE856C; }

.reaffirmation { background:#F1F4EF; border-radius:14px; padding:4.5mm 8mm 5mm; margin:4.5mm 0; text-align:center; break-inside:avoid; }
.reaffirmation .label { color:#3D8186; }
.reaffirmation .label::after { content:''; display:inline-block; width:13px; height:2px; background:#3D8186; vertical-align:middle; margin-left:7px; margin-bottom:3px; }
.reaffirmation .label::before { width:13px; background:#3D8186; }
.affirm { font-family:'Nunito',sans-serif; font-weight:600; font-style:italic; font-size:12.8pt; line-height:1.42; color:#001B39; margin:2.5mm 0 0; }
.affirm .qm { font-family:'Fredoka',sans-serif; font-weight:600; font-style:normal; color:#FE856C; }

.evening { background:#F6EFE2; border-radius:11px; padding:10px 15px; margin:4mm 0 0; break-inside:avoid; }
.evening .label { margin-bottom:3px; }
.evening-q { font-family:'Nunito',sans-serif; font-weight:700; font-size:11pt; color:#001B39; margin:0; }
"""

# ---------------------------------------------------------------- assemble
def build(days):
    body = [cover_html(), preface_html(), toc_html()]
    # group the requested days by phase, emitting a divider before each phase
    for kicker, name, rng, _ in PHASES:
        phase_days = [d for d in days if d in rng]
        if not phase_days:
            continue
        body.append(divider_html(kicker, name, rng))
        body.extend(day_html(n) for n in phase_days)
    if 30 in days:
        body.append(closing_html())
    html = (
        '<!DOCTYPE html><html lang="en"><head><meta charset="utf-8">'
        f"<style>{CSS}</style></head><body>{''.join(body)}</body></html>"
    )
    return html

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--all", action="store_true", help="render all 30 days")
    ap.add_argument("--out", default=None)
    args = ap.parse_args()

    if args.all:
        days = list(range(1, 31))
        out = args.out or str(REPO / "content/journal_30day_en.pdf")
    else:
        days = list(range(1, 8))
        out = args.out or str(REPO / "content/journal_30day_en_sample.pdf")

    html = build(days)
    (BUILD / "_preview.html").write_text(html)
    HTML(string=html, base_url=str(BUILD)).write_pdf(out)
    print("Wrote", out, "(", len(days), "days )")

if __name__ == "__main__":
    main()
