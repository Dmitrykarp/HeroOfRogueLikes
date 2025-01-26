package map;

import java.util.ArrayList;
import java.util.Random;

public class GameMap {
    private final int width;
    private final int height;
    private final char[][] map;
    private final Random random = new Random();

    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.map = new char[height][width];
    }

    public void generateMapWithRooms() {
        // Очистка карты
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = '#'; // Всё заполняется стенами
            }
        }

        // Генерация комнат
        ArrayList<int[]> rooms = new ArrayList<>();
        int numRooms = random.nextInt(5) + 5;

        for (int i = 0; i < numRooms; i++) {
            int roomWidth = random.nextInt(6 - 3 + 1) + 3;
            int roomHeight = random.nextInt(6 - 3 + 1) + 3;
            int roomX = random.nextInt(width - roomWidth - 1) + 1;
            int roomY = random.nextInt(height - roomHeight - 1) + 1;

            boolean intersects = false;
            for (int[] otherRoom : rooms) {
                if (roomX < otherRoom[0] + otherRoom[2] && roomX + roomWidth > otherRoom[0] &&
                        roomY < otherRoom[1] + otherRoom[3] && roomY + roomHeight > otherRoom[1]) {
                    intersects = true;
                    break;
                }
            }

            if (!intersects) {
                rooms.add(new int[]{roomX, roomY, roomWidth, roomHeight});
                carveRoom(roomX, roomY, roomWidth, roomHeight);

                if (rooms.size() > 1) {
                    int[] prevRoom = rooms.get(rooms.size() - 2);
                    carveCorridor(prevRoom[0] + prevRoom[2] / 2, prevRoom[1] + prevRoom[3] / 2,
                            roomX + roomWidth / 2, roomY + roomHeight / 2);
                }
            }
        }

        // Размещаем выход
        int[] lastRoom = rooms.get(rooms.size() - 1);
        map[lastRoom[1] + lastRoom[3] / 2][lastRoom[0] + lastRoom[2] / 2] = 'X';
    }

    private void carveRoom(int x, int y, int width, int height) {
        for (int i = y; i < y + height; i++) {
            for (int j = x; j < x + width; j++) {
                map[i][j] = '.';
            }
        }
    }

    private void carveCorridor(int x1, int y1, int x2, int y2) {
        while (x1 != x2) {
            map[y1][x1] = '.';
            x1 += x1 < x2 ? 1 : -1;
        }
        while (y1 != y2) {
            map[y1][x1] = '.';
            y1 += y1 < y2 ? 1 : -1;
        }
    }

    public char getTile(int x, int y) {
        return map[y][x];
    }

    public void setTile(int x, int y, char tile) {
        map[y][x] = tile;
    }

    public void printMap() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(map[y][x] + " ");
            }
            System.out.println();
        }
    }

    public boolean isWalkable(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height && map[y][x] != '#';
    }

    public boolean isExit(int x, int y) {
        return map[y][x] == 'X';
    }
}