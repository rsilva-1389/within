#!/usr/bin/env python3
"""
Within — Phase 0: manuscript -> app content.

Parses content/journal_30day_en.md (the same single source of truth the PDF
uses) and emits app/src/main/res/raw/journey_en.json in the schema described in
JOURNEY_ENGINE.md. Stdlib only — no third-party deps.

    python3 content/build/generate_journey_json.py
    python3 content/build/generate_journey_json.py --input content/journal_30day_pt_br.md --lang pt_br

Text handling:
  - markdown emphasis (*..* / **..**) is flattened to plain text
  - straight quotes/apostrophes become typographic ones (matches the PDF)
  - reflections become an array of paragraphs; deep-dives an array of 3
"""
import re
import json
import argparse
import pathlib

REPO = pathlib.Path("/home/rsilva/projetos/within")

# product configuration (decisions, not manuscript content)
JOURNEY_ID = "coming-home"
TIER = "premium"
FREE_PREVIEW_DAYS = 7

# (id, display title, first day, last day) — must cover every day. The titles must match the
# manuscript's `# Phase N · <title>` headings so phase intros can be found, and become the JSON
# phase titles. Per-locale because the headings are translated; `PHASES` is selected in main().
# A pt-BR manuscript must keep the *structural* `**Theme:**`/`**Reflection**`/… section labels
# (LBL below) in English so the parser still slices days — translate the content, not the markers.
PHASES_BY_LANG = {
    "en": [
        ("self_acceptance", "Self-Acceptance", 1, 7),
        ("empowerment", "Empowerment", 8, 15),
        ("connection", "Connection", 16, 23),
        ("purpose", "Purpose", 24, 30),
    ],
    "pt_br": [
        ("self_acceptance", "Autoaceitação", 1, 7),
        ("empowerment", "Empoderamento", 8, 15),
        ("connection", "Conexão", 16, 23),
        ("purpose", "Propósito", 24, 30),
    ],
}
PHASES = PHASES_BY_LANG["en"]  # default; main() overrides from --lang

LBL = [
    "**Theme:**", "**Reflection**", "**Journal Prompt**", "**Deep-Dive Questions**",
    "**Reaffirmation**", "**Mindful Action**", "**Evening Reflection**",
]

# ----------------------------------------------------------------- text helpers
def smart(s):
    """Straight quotes/apostrophes -> typographic ones."""
    s = s.replace("'", "’")
    out, opening = [], True
    for ch in s:
        if ch == '"':
            out.append("“" if opening else "”")
            opening = not opening
        else:
            out.append(ch)
    return "".join(out)

def clean(s):
    """Flatten markdown emphasis, normalize whitespace, apply typography."""
    s = re.sub(r"\*\*(.+?)\*\*", r"\1", s)
    s = re.sub(r"\*(.+?)\*", r"\1", s)
    s = re.sub(r"\s+", " ", s).strip()
    return smart(s)

# ----------------------------------------------------------------- structural parse
def slice_sections(body):
    """Slice a day body into {label: raw text} by the seven section labels."""
    found = [(lab, body.find(lab)) for lab in LBL]
    secs = {}
    for lab, p in found:
        if p < 0:
            continue
        start = p + len(lab)
        later = [pp for _, pp in found if pp > p]
        if later:
            end = min(later)
        else:  # last section: stop at the day's closing horizontal rule
            dash = body.find("\n---", start)
            end = dash if dash >= 0 else len(body)
        secs[lab] = body[start:end].strip()
    return secs

def parse_days(md):
    day_re = re.compile(r"^### Day (\d+) · (.+?)\s*$", re.M)
    ms = list(day_re.finditer(md))
    days = []
    for i, m in enumerate(ms):
        start = m.end()
        end = ms[i + 1].start() if i + 1 < len(ms) else len(md)
        days.append((int(m.group(1)), m.group(2).strip(), md[start:end]))
    return days

def phase_for(day):
    for pid, _, a, b in PHASES:
        if a <= day <= b:
            return pid
    raise ValueError(f"day {day} is outside every phase range")

def phase_intro(md, name):
    m = re.search(rf"#\s+Phase \w+ · {re.escape(name)}\s*\n\s*\*(.+?)\*\s*\n", md, re.S)
    return clean(m.group(1)) if m else ""

def meta(md):
    h1 = re.search(r"^#\s+(.+)$", md, re.M).group(1).strip()
    title = h1.split("—")[-1].strip() if "—" in h1 else h1
    subtitle = re.search(r"^###\s+(.+)$", md, re.M).group(1).strip()
    tagline = re.search(r"^\*(.+?)\*\s*$", md, re.M).group(1).strip()
    return title, subtitle, clean(tagline)

# ----------------------------------------------------------------- build
def build_day(num, title, body):
    s = slice_sections(body)
    paragraphs = [clean(p) for p in re.split(r"\n\s*\n", s["**Reflection**"].strip()) if p.strip()]
    deep_dive = [
        clean(re.sub(r"^\d+\.\s+", "", ln.strip()))
        for ln in s["**Deep-Dive Questions**"].split("\n")
        if re.match(r"^\d+\.", ln.strip())
    ]
    reaffirmation = clean(s["**Reaffirmation**"].lstrip("> ").strip()).strip("“”\"").strip()
    return {
        "day": num,
        "phaseId": phase_for(num),
        "title": clean(title),
        "theme": clean(s["**Theme:**"]),
        "reflection": paragraphs,
        "prompt": clean(s["**Journal Prompt**"].lstrip("> ").strip()),
        "deepDive": deep_dive,
        "reaffirmation": reaffirmation,
        "mindfulAction": clean(s["**Mindful Action**"]),
        "eveningReflection": clean(s["**Evening Reflection**"].lstrip("> ").strip()),
    }

def build(md):
    title, subtitle, tagline = meta(md)
    days = parse_days(md)
    day_objs = [build_day(n, t, b) for n, t, b in days]
    journey = {
        "id": JOURNEY_ID,
        "title": title,
        "subtitle": subtitle,
        "tagline": tagline,
        "lengthDays": len(day_objs),
        "tier": TIER,
        "freePreviewDays": FREE_PREVIEW_DAYS,
        "phases": [
            {"id": pid, "title": name, "from": a, "to": b, "intro": phase_intro(md, name)}
            for pid, name, a, b in PHASES
        ],
        "days": day_objs,
    }
    validate(journey)
    return journey

def validate(j):
    assert j["lengthDays"] == 30, f"expected 30 days, got {j['lengthDays']}"
    seen = {d["day"] for d in j["days"]}
    assert seen == set(range(1, 31)), f"missing/extra days: {sorted(set(range(1, 31)) ^ seen)}"
    for d in j["days"]:
        assert d["reflection"], f"day {d['day']}: empty reflection"
        assert len(d["deepDive"]) == 3, f"day {d['day']}: {len(d['deepDive'])} deep-dive Qs (expected 3)"
        for k in ("title", "theme", "prompt", "reaffirmation", "mindfulAction", "eveningReflection"):
            assert d[k].strip(), f"day {d['day']}: empty {k}"
    for p in j["phases"]:
        assert p["intro"], f"phase {p['id']}: empty intro"

# ----------------------------------------------------------------- main
def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--input", default=str(REPO / "content/journal_30day_en.md"))
    ap.add_argument("--lang", default="en")
    ap.add_argument("--out", default=None)
    args = ap.parse_args()

    global PHASES
    if args.lang not in PHASES_BY_LANG:
        ap.error(f"--lang {args.lang!r} has no phase titles; add it to PHASES_BY_LANG")
    PHASES = PHASES_BY_LANG[args.lang]

    md = pathlib.Path(args.input).read_text()
    journey = build(md)
    out = pathlib.Path(args.out or REPO / f"app/src/main/res/raw/journey_{args.lang}.json")
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(json.dumps(journey, ensure_ascii=False, indent=2) + "\n")
    print(f"Wrote {out}")
    print(f"  {journey['lengthDays']} days · {len(journey['phases'])} phases · "
          f"tier={journey['tier']} · freePreview={journey['freePreviewDays']}")

if __name__ == "__main__":
    main()
