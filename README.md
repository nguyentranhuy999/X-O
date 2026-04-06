# X-O

Dự án game caro (X-O) viết bằng Java Swing, có bot sử dụng minimax + alpha-beta và nhiều lớp tối ưu để giữ tốc độ nhanh ngay cả khi bot-vs-bot.

## Tổng quan bot

Bot nằm ở file `src/Bot/Own.java`.

Mục tiêu của bot:
- Tìm nước đi tốt trong thời gian ngắn.
- Vẫn phòng thủ/tấn công ổn trong các thế cờ nguy hiểm.
- Giữ chi phí tính toán thấp để game chạy mượt.

Luồng xử lý chính cho mỗi nước đi:
1. Khởi tạo `frontier` từ trạng thái bàn hiện tại (`initializeFrontierState`).
2. Kiểm tra tactical ngay lập tức: nếu bot có nước thắng ngay thì đánh ngay, nếu đối thủ có nước thắng ngay thì chặn ngay.
3. Sinh tập nước ứng viên từ `frontier` (`getAvailableMoves`).
4. Sắp xếp nước đi (move ordering).
5. Chạy minimax + alpha-beta + transposition table.
6. Chọn nước có điểm tốt nhất.

## Minimax và leaf evaluation

Bot dùng minimax với alpha-beta pruning.

Leaf evaluation (khi depth = 0 hoặc hết nước) được tính bằng:
- `evaluateBoard(botTurn) = botScore - 1.2 * opponentScore`

Trong đó mỗi bên được chấm điểm theo pattern:
- 5 liên tiếp: `10_000_000`
- 4 mở 2 đầu: `500_000`
- 4 mở 1 đầu: `50_000`
- 3 mở 2 đầu: `20_000`
- 3 mở 1 đầu: `2_000`
- 2 mở 2 đầu: `500`
- 2 mở 1 đầu: `50`
- 1 mở 2 đầu: `10`

Hướng duyệt pattern: ngang, dọc, chéo chính, chéo phụ.

## Các tối ưu đã áp dụng

### 1) Alpha-beta pruning
- Cắt nhánh không cần thiết khi `beta <= alpha`.
- Giảm số node phải mở rộng so với minimax thuần.

### 2) Tactical short-circuit
- Thắng ngay / chặn thua ngay được xử lý trước minimax.
- Tránh bỏ sót các tình huống “bắt buộc”.

### 3) Move ordering
- Nước đi được sắp xếp theo `quickMoveOrderScore`.
- Ưu tiên cao cho nước thắng ngay, nước chặn thua ngay, nước tạo threat mạnh (`linePotentialScore`) và nước có nhiều hàng xóm (`neighborScore`).
- Sắp xếp tốt giúp alpha-beta cắt nhanh hơn rất nhiều.

### 4) Incremental candidate frontier
Thay vì quét toàn bộ bàn mỗi node:
- Bot duy trì `frontierCells` (ô trống gần khu vực có quân).
- Mỗi lần `applyMove/undoMove` chỉ update vùng quanh nước vừa đánh (bán kính `SEARCH_RADIUS = 2`).
- `getAvailableMoves` lấy trực tiếp từ frontier.

Lợi ích:
- Giảm chi phí sinh nước trong đệ quy.
- Tăng tốc độ rõ trong bot-vs-bot.

### 5) Transposition Table + Zobrist Hash
- Mỗi trạng thái bàn cờ được băm hash bởi Zobrist.
- Cache kết quả node minimax với 3 loại cờ: `EXACT`, `LOWER_BOUND`, `UPPER_BOUND`.
- Tái sử dụng kết quả node đã tính ở các nhánh khác.
- Có giới hạn kích thước bảng cache (`TT_MAX_SIZE`) để tránh phình bộ nhớ.

### 6) Cached move promotion
- Nếu TT có nước tốt nhất của node, đưa nước đó lên đầu danh sách duyệt.
- Giữ move ordering tốt ngay cả khi đệ quy sâu.

### 7) Dynamic search depth
Depth không cố định:
- Đầu ván: depth 2
- Giữa ván: depth 3
- Cuối ván (ít candidate): depth 4

Mục tiêu:
- Đầu game giữ tốc độ.
- Cuối game tăng độ chính xác.

## Các hằng số quan trọng (để tune)

Nằm trong `src/Bot/Own.java`:
- `MAX_CANDIDATE_MOVES = 32`
- `SEARCH_RADIUS = 2`
- `EARLY_GAME_DEPTH = 2`
- `MID_GAME_DEPTH = 3`
- `LATE_GAME_DEPTH = 4`
- `TT_MAX_SIZE = 400_000`

Nếu muốn bot mạnh hơn:
- Tăng `MAX_CANDIDATE_MOVES` hoặc depth (đổi lại chậm hơn).

Nếu muốn bot mát hơn:
- Giảm `MAX_CANDIDATE_MOVES`, giảm depth hoặc giảm `TT_MAX_SIZE`.

## Chạy project

### Trong VS Code
1. Cài extension `Extension Pack for Java`.
2. Mở thư mục project.
3. Chọn cấu hình Run/Debug `Run X-O`.
4. Nhấn `F5`.

### Bằng terminal
```bash
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out:src main.Main
```

## Ghi chú hiện tại

- Bot đã mạnh hơn bản gốc rất nhiều nhờ tactical + evaluation + ordering + cache.
- Bot-vs-bot vẫn có thể dùng CPU cao do minimax đệ quy liên tục, nhưng đã được tối ưu tính toán đáng kể so với bản đầu.

