"""Shared-base 32px seal stamping; no resizing, palette filtering or alpha changes."""
from pathlib import Path
from PIL import Image,ImageDraw
import hashlib,json
ROOT=Path(__file__).resolve().parents[1]
base=ROOT/'common/src/main/resources/assets/goety_charms/textures/item/blank_charm.png'
image=Image.open(base).convert('RGBA')
assert image.size==(32,32)
stamps={
 'summoning_charm':[((12,18),(18,18)),((12,18),(12,23)),((18,18),(18,23)),((12,23),(18,23)),((15,16),(15,18)),((15,23),(15,25)),((14,20),(16,20)),((15,20),(15,22))],
 'conversion_charm':[((12,19),(17,19)),((17,19),(15,17)),((17,19),(15,21)),((18,23),(13,23)),((13,23),(15,21)),((13,23),(15,25))],
 'teleportation_charm':[((12,24),(12,17)),((12,17),(18,17)),((18,17),(18,24)),((14,24),(16,24)),((15,23),(15,20)),((15,20),(13,22)),((15,20),(17,22))],
 'location_charm':[((15,16),(15,25)),((11,21),(19,21)),((15,17),(18,21)),((18,21),(15,24)),((15,24),(12,21)),((12,21),(15,17))]
}
colors={
 'summoning_charm':((29,66,60,255),(77,151,127,255)),
 'conversion_charm':((69,37,81,255),(148,83,155,255)),
 'teleportation_charm':((25,57,81,255),(60,159,185,255)),
 'location_charm':((87,54,24,255),(188,137,48,255))
}
# Center on the light plaque face, excluding the cord and the beveled rim.
STAMP_OFFSET=(-1,-2)
for name,lines in stamps.items():
 lines=[((a[0]+STAMP_OFFSET[0],a[1]+STAMP_OFFSET[1]),(b[0]+STAMP_OFFSET[0],b[1]+STAMP_OFFSET[1])) for a,b in lines]
 out=image.copy();draw=ImageDraw.Draw(out)
 dark,light=colors[name]
 for a,b in lines:draw.line((a[0]+1,a[1]+1,b[0]+1,b[1]+1),fill=dark,width=1)
 for a,b in lines:draw.line((*a,*b),fill=light,width=1)
 out.putalpha(image.getchannel('A'))
 path=ROOT/'art/work'/f'{name}-stamped.png';path.parent.mkdir(parents=True,exist_ok=True);out.save(path)
 record={'algorithm':'shared-charm-stamp-2','stamp_offset':STAMP_OFFSET,'base_sha256':hashlib.sha256(base.read_bytes()).hexdigest(),'output_sha256':hashlib.sha256(path.read_bytes()).hexdigest(),'invariant':'32px silhouette and alpha unchanged; only central seal differs','stamp':lines}
 (ROOT/'art/pixel/records'/f'{name}-stamp.json').write_text(json.dumps(record,indent=2)+'\n',encoding='utf-8')
 print(path)
