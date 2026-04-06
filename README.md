# X-O

Du an game caro (X-O) viet bang Java Swing, co bot su dung minimax + alpha-beta va nhieu lop toi uu de giu toc do nhanh ngay ca khi bot-vs-bot.

## Tong quan bot

Bot nam o file `src/Bot/Own.java`.

Muc tieu cua bot:
- Tim nuoc di tot trong thoi gian ngan.
- Van phong thu/tan cong on trong cac the co nguy hiem.
- Giu chi phi tinh toan thap de game chay muot.

Luot xu ly chinh cho moi nuoc di:
1. Khoi tao `frontier` tu trang thai ban hien tai (`initializeFrontierState`).
2. Kiem tra tactical ngay lap tuc: neu bot co nuoc thang ngay thi danh ngay, neu doi thu co nuoc thang ngay thi chan ngay.
3. Sinh tap nuoc ung vien tu `frontier` (`getAvailableMoves`).
4. Sap xep nuoc di (move ordering).
5. Chay minimax + alpha-beta + transposition table.
6. Chon nuoc co diem tot nhat.

## Minimax va leaf evaluation

Bot dung minimax voi alpha-beta pruning.

Leaf evaluation (khi depth = 0 hoac het nuoc) duoc tinh bang:
- `evaluateBoard(botTurn) = botScore - 1.2 * opponentScore`

Trong do moi ben duoc cham diem theo pattern:
- 5 lien tiep: `10_000_000`
- 4 mo 2 dau: `500_000`
- 4 mo 1 dau: `50_000`
- 3 mo 2 dau: `20_000`
- 3 mo 1 dau: `2_000`
- 2 mo 2 dau: `500`
- 2 mo 1 dau: `50`
- 1 mo 2 dau: `10`

Huong duyet pattern: ngang, doc, cheo chinh, cheo phu.

## Cac toi uu da ap dung

### 1) Alpha-beta pruning
- Cat nhanh nhanh khong can thiet khi `beta <= alpha`.
- Giam so node phai mo rong so voi minimax thuan.

### 2) Tactical short-circuit
- Thang ngay / chan thua ngay duoc xu ly truoc minimax.
- Tranh bo sot cac tinh huong "bat buoc".

### 3) Move ordering
- Nuoc di duoc sap xep theo `quickMoveOrderScore`.
- Uu tien cao cho nuoc thang ngay, nuoc chan thua ngay, nuoc tao threat manh (`linePotentialScore`) va nuoc co nhieu hang xom (`neighborScore`).
- Sap xep tot giup alpha-beta cat nhanh hon rat nhieu.

### 4) Incremental candidate frontier
Thay vi quet toan bo ban moi node:
- Bot duy tri `frontierCells` (o trong gan khu vuc co quan).
- Moi lan `applyMove/undoMove` chi update vung quanh nuoc vua danh (ban kinh `SEARCH_RADIUS = 2`).
- `getAvailableMoves` lay truc tiep tu frontier.

Loi ich:
- Giam chi phi sinh nuoc trong de quy.
- Tang toc do ro trong bot-vs-bot.

### 5) Transposition Table + Zobrist Hash
- Moi trang thai ban co duoc bam hash boi Zobrist.
- Cache ket qua node minimax voi 3 loai co: `EXACT`, `LOWER_BOUND`, `UPPER_BOUND`.
- Tai su dung ket qua node da tinh o cac nhanh khac.
- Co gioi han kich thuoc bang cache (`TT_MAX_SIZE`) de tranh phinh bo nho.

### 6) Cached move promotion
- Neu TT co nuoc tot nhat cua node, dua nuoc do len dau danh sach duyet.
- Giu move ordering ngay ca khi de quy sau.

### 7) Dynamic search depth
Depth khong co dinh:
- Dau van: depth 2
- Giua van: depth 3
- Cuoi van (it candidate): depth 4

Muc tieu:
- Dau game giu toc do.
- Cuoi game tang do chinh xac.

## Cac hang so quan trong (de tune)

Nam trong `src/Bot/Own.java`:
- `MAX_CANDIDATE_MOVES = 32`
- `SEARCH_RADIUS = 2`
- `EARLY_GAME_DEPTH = 2`
- `MID_GAME_DEPTH = 3`
- `LATE_GAME_DEPTH = 4`
- `TT_MAX_SIZE = 400_000`

Neu muon bot manh hon:
- Tang `MAX_CANDIDATE_MOVES` hoac depth (doi lai cham hon).

Neu muon bot mat hon:
- Giam `MAX_CANDIDATE_MOVES`, giam depth hoac giam `TT_MAX_SIZE`.

## Chay project

### Trong VS Code
1. Cai extension `Extension Pack for Java`.
2. Mo thu muc project.
3. Chon cau hinh Run/Debug `Run X-O`.
4. Nhan `F5`.

### Bang terminal
```bash
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out:src main.Main
```

## Ghi chu hien tai

- Bot da manh hon ban goc rat nhieu nho tactical + evaluation + ordering + cache.
- Bot-vs-bot van co the dung CPU cao do minimax de quy lien tuc, nhung da duoc toi uu tinh toan dang ke so voi ban dau.
