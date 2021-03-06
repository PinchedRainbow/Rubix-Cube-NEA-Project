package com.amg.rubik.cubeOriginal;

import com.amg.rubik.Log;
import com.amg.rubik.graphics.Axis;
import com.amg.rubik.graphics.Direction;

import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * This class handles cubeOriginal's definition. It creates all squares, faces and puts them in appropriate
 * lists for each axes. It also takes care of updating the colors of squares according to user
 * specified rotation. It doesn't care about drawing the cubeOriginal. You should extend this class
 * rather than using it directly.
 */
public class Cube {
    private static final String tag = "rubik-struct";

    // Do not change these values. They are used in many places.
    public static final int FACE_FRONT = 0;
    public static final int FACE_RIGHT = 1;
    public static final int FACE_BACK = 2;
    public static final int FACE_LEFT = 3;
    public static final int FACE_TOP = 4;
    public static final int FACE_BOTTOM = 5;

    /**
     * Default colors don't look nice on cubeOriginal.
     * Note: Colors are in RBGA (for libgdx)
     * */
    public static final int Color_RED = 0xDD2211FF;
    public static final int Color_GREEN = 0x22DD11FF;
    public static final int Color_ORANGE = 0xFF7F10FF;
    public static final int Color_WHITE = 0xFFFFFFFF;
    public static final int Color_YELLOW = 0xFFFF00FF;
    public static final int Color_BLUE = 0x0000FFFF;
    public static final int Color_GRAY = 0x7F7F7FFF;

    public static int COLOR_TOP = Color_WHITE;
    public static int COLOR_BOTTOM = Color_YELLOW;
    public static int COLOR_LEFT = Color_ORANGE;
    public static int COLOR_RIGHT = Color_RED;
    public static int COLOR_FRONT = Color_BLUE;
    public static int COLOR_BACK = Color_GREEN;


    private static final String[] faceNames = {
            "front", "right", "back", "left", "top", "bottom"
    };

    // We don't support skewed cubes yet.
    static final int CUBE_SIDES = 4;
    static final int FACE_COUNT = 6;

    /**
     * To calculate the square size:
     * Screen spans from -1f to +1f.
     * OpenGl won't draw things close to the frustrum border, hence we add padding and use
     * 1.2f instead of 2.0f as the total size
     * */
    private static final float TOTAL_SIZE = 8.0f;
    private static final float PADDING = 0.8f;

    private static final float GAP = 0.1f;

    private int mSizeX;
    private int mSizeY;
    private int mSizeZ;
    private final float squareSize;

    ArrayList<Square> mAllSquares;
    ArrayList<Square> mFrontSquares;
    ArrayList<Square> mBackSquares;
    ArrayList<Square> mTopSquares;
    ArrayList<Square> mBottomSquares;
    ArrayList<Square> mLeftSquares;
    ArrayList<Square> mRightSquares;
    ArrayList<Square>[] mAllFaces;

    /**
     * Pieces are used to draw squares during animation. We keep separate sets of layers for each
     * axis and animate pieces from the selected layer of the appropriate set during rotation.
     * */
    private ArrayList<Piece> mAllPieces;
    ArrayList<ArrayList<Piece>> mXaxisLayers;
    ArrayList<ArrayList<Piece>> mYaxisLayers;
    ArrayList<ArrayList<Piece>> mZaxisLayers;

    // Clockwise list of faces along each axes
    public static final int[] orderedFacesXaxis = {FACE_FRONT, FACE_TOP, FACE_BACK, FACE_BOTTOM};
    public static final int[] orderedFacesYaxis = {FACE_FRONT, FACE_LEFT, FACE_BACK, FACE_RIGHT};
    public static final int[] orderedFacesZaxis = {FACE_TOP, FACE_RIGHT, FACE_BOTTOM, FACE_LEFT};

    public Cube(int sizeX, int sizeY, int sizeZ) {
        mSizeX = sizeX;
        mSizeY = sizeY;
        mSizeZ = sizeZ;
        Log.w(tag, String.format("Cube_Original Dimen: %d %d %d", sizeX, sizeY, sizeZ));
        int maxSize = Math.max(Math.max(sizeX, sizeY), sizeZ);
        squareSize = (TOTAL_SIZE - PADDING - GAP * (maxSize + 1)) / maxSize;
        cubeOriginal();
    }

    private void cubeOriginal()
    {
        mAllSquares = new ArrayList<>();
        mFrontSquares = new ArrayList<>();
        mBackSquares = new ArrayList<>();
        mTopSquares = new ArrayList<>();
        mBottomSquares = new ArrayList<>();
        mLeftSquares = new ArrayList<>();
        mRightSquares = new ArrayList<>();
        mAllFaces = new ArrayList[FACE_COUNT];
        createAllSquares();

        createFaces();
    }

    private static Piece.PieceType getPieceType(int row, int col, int totalRows, int totalCols) {
        if (row == 0 || row == totalRows - 1) {
            return (col == 0 || col == totalCols - 1) ?
                    Piece.PieceType.CORNER : Piece.PieceType.EDGE;
        } else if (col == 0 || col == totalCols - 1) {
            return Piece.PieceType.EDGE;
        } else {
            return Piece.PieceType.CENTER;
        }
    }

    /**
     * Create the squares and store them in their respective lists
     *
     * On each face array, (the respective m***Squares array), the squares are stored from the
     * top-left to the bottom-right order assuming you're looking directly at that face.
     * This order is used in various places and hence should not be changed.
     * */
    private void createAllSquares() {
        createFrontSquares(COLOR_FRONT);
        createBackSquares(COLOR_BACK);
        createLeftSquares(COLOR_LEFT);
        createRightSquares(COLOR_RIGHT);
        createTopSquares(COLOR_TOP);
        createBottomSquares(COLOR_BOTTOM);
    }

    /**
     * From top-far to bottom-near. YZ
     * Y moves down after filling each row on Z axis
     *
     * On negative X plane
     * */
    private void createLeftSquares(int color)
    {
        float startX = getLeftFaceX();
        float startY = (squareSize + GAP) * (mSizeY / 2.0f);
        float startZ = 0 - (squareSize + GAP) * (mSizeZ / 2.0f);

        float[] vertices = {
                startX,  startY, startZ,
                startX,  startY - squareSize, startZ,
                startX,  startY - squareSize, startZ + squareSize,
                startX,  startY, startZ + squareSize
        };

        for (int i = 0; i < mSizeY; i++) {
            vertices[1] = startY - i * (squareSize + GAP);
            vertices[4] = vertices[1] - squareSize;
            vertices[7] = vertices[1] - squareSize;
            vertices[10] = vertices[1];

            for (int j = 0; j < mSizeZ; j++) {
                vertices[2] = startZ + j * (squareSize + GAP);
                vertices[5] = vertices[2];
                vertices[8] = vertices[2] + squareSize;
                vertices[11] = vertices[2] + squareSize;
                Square sq = new Square(vertices, color, FACE_LEFT);
                mAllSquares.add(sq);
                mLeftSquares.add(sq);
            }
        }
    }

    /**
     * From top-near to bottom-far. YZ
     * Y moves down after filling each row on Z axis
     *
     * On positive X plane
     * */
    private void createRightSquares(int color)
    {
        float startX = getRightFaceX();
        float startY = (squareSize + GAP) * (mSizeY / 2.0f);
        float startZ = (squareSize + GAP) * (mSizeZ / 2.0f);

        float[] vertices = {
                startX,  startY, startZ,
                startX,  startY - squareSize, startZ,
                startX,  startY - squareSize, startZ - squareSize,
                startX,  startY, startZ - squareSize
        };

        for (int i = 0; i < mSizeY; i++) {
            vertices[1] = startY - i * (squareSize + GAP);
            vertices[4] = vertices[1] - squareSize;
            vertices[7] = vertices[1] - squareSize;
            vertices[10] = vertices[1];

            for (int j = 0; j < mSizeZ; j++) {
                vertices[2] = startZ - j * (squareSize + GAP);
                vertices[5] = vertices[2];
                vertices[8] = vertices[2] - squareSize;
                vertices[11] = vertices[2] - squareSize;
                Square sq = new Square(vertices, color, FACE_RIGHT);
                mAllSquares.add(sq);
                mRightSquares.add(sq);
            }
        }
    }

    /**
     * From far-left to near-right. ZX
     * Z moves closer after filling each row on X axis
     *
     * On positive Y plane
     * */
    private void createTopSquares(int color)
    {
        float startX = - (squareSize + GAP) * (mSizeX / 2.0f);
        float startY = getTopFaceY();
        float startZ = - (squareSize + GAP) * (mSizeZ / 2.0f);

        float[] vertices = {
                startX,  startY, startZ,
                startX,  startY, startZ + squareSize,
                startX + squareSize,  startY, startZ + squareSize,
                startX + squareSize,  startY, startZ
        };

        for (int i = 0; i < mSizeZ; i++) {
            vertices[2] = startZ + i * (squareSize + GAP);
            vertices[5] = vertices[2] + squareSize;
            vertices[8] = vertices[2] + squareSize;
            vertices[11] = vertices[2];

            for (int j = 0; j < mSizeX; j++) {
                vertices[0] = startX + j * (squareSize + GAP);
                vertices[3] = vertices[0];
                vertices[6] = vertices[0] + squareSize;
                vertices[9] = vertices[0] + squareSize;
                Square sq = new Square(vertices, color, FACE_TOP);
                mAllSquares.add(sq);
                mTopSquares.add(sq);
            }
        }
    }

    /**
     * From near-left to far-right. ZX
     * Z moves further after filling each row on X axis
     *
     * On negative Y plane
     * */
    private void createBottomSquares(int color)
    {
        float startX = - (squareSize + GAP) * (mSizeX / 2.0f);
        float startY = getBottomFaceY();
        float startZ = (squareSize + GAP) * (mSizeZ / 2.0f);

        float vertices[] = {
                startX,  startY, startZ,
                startX,  startY, startZ - squareSize,
                startX + squareSize,  startY, startZ - squareSize,
                startX + squareSize,  startY, startZ
        };

        for (int i = 0; i < mSizeZ; i++) {
            vertices[2] = startZ - i * (squareSize + GAP);
            vertices[5] = vertices[2] - squareSize;
            vertices[8] = vertices[2] - squareSize;
            vertices[11] = vertices[2];

            for (int j = 0; j < mSizeX; j++) {
                vertices[0] = startX + j * (squareSize + GAP);
                vertices[3] = vertices[0];
                vertices[6] = vertices[0] + squareSize;
                vertices[9] = vertices[0] + squareSize;
                Square sq = new Square(vertices, color, FACE_BOTTOM);
                mAllSquares.add(sq);
                mBottomSquares.add(sq);
            }
        }
    }

    /**
     * From top-left to bottom-right.
     * Y moves down after filling each row on X axis
     *
     * On positive z (near plane).
     * */
    private void createFrontSquares(int color) {
        float startX = 0 - (squareSize + GAP) * (mSizeX / 2.0f);
        float startY = (squareSize + GAP) * (mSizeY / 2.0f);
        float startZ = getFrontFaceZ();

        float vertices[] = {
                startX,  startY, startZ,
                startX,  startY - squareSize, startZ,
                startX + squareSize,  startY - squareSize, startZ,
                startX + squareSize,  startY, startZ
        };

        for (int i = 0; i < mSizeY; i++) {
            vertices[1] = startY - i * (squareSize + GAP);
            vertices[4] = vertices[1] - squareSize;
            vertices[7] = vertices[1] - squareSize;
            vertices[10] = vertices[1];

            for (int j = 0; j < mSizeX; j++) {
                vertices[0] = startX + j * (squareSize + GAP);
                vertices[3] = vertices[0];
                vertices[6] = vertices[0] + squareSize;
                vertices[9] = vertices[0] + squareSize;
                Square sq = new Square(vertices, color, FACE_FRONT);
                mAllSquares.add(sq);
                mFrontSquares.add(sq);
            }
        }
    }

    /**
     * From top-right to bottom-left.
     * Y moves down after filling each row on X axis
     *
     * On negative z (far plane).
     * */
    private void createBackSquares(int color) {
        float startX = (squareSize + GAP) * (mSizeX / 2.0f);
        float startY = (squareSize + GAP) * (mSizeY / 2.0f);
        float startZ = getBackFaceZ();

        float vertices[] = {
                startX,  startY, startZ,
                startX,  startY - squareSize, startZ,
                startX - squareSize,  startY - squareSize, startZ,
                startX - squareSize,  startY, startZ
        };

        for (int i = 0; i < mSizeY; i++) {
            vertices[1] = startY - i * (squareSize + GAP);
            vertices[4] = vertices[1] - squareSize;
            vertices[7] = vertices[1] - squareSize;
            vertices[10] = vertices[1];

            for (int j = 0; j < mSizeX; j++) {
                vertices[0] = startX - j * (squareSize + GAP);
                vertices[3] = vertices[0];
                vertices[6] = vertices[0] - squareSize;
                vertices[9] = vertices[0] - squareSize;
                Square sq = new Square(vertices, color, FACE_BACK);
                mAllSquares.add(sq);
                mBackSquares.add(sq);
            }
        }
    }

    /**
     * Create a new piece or return an existing piece that contains one of the squares. A square
     * can only be part of one piece. This ensures that there are no duplicate or partial pieces.
     * */
    private Piece createPieceWithSquares(ArrayList<Square> squares, Piece.PieceType type) {
        Piece piece = null;
        for (Piece p: mAllPieces) {
            for (Square sq: squares) {
                if (p.mSquares.contains(sq)) {
                    piece = p;
                    break;
                }
            }
            if (piece != null) break;
        }
        if (piece == null) {
            piece = new Piece(type);
            mAllPieces.add(piece);
        }
        for (Square sq: squares) {
            piece.addSquare(sq);
        }
        return piece;
    }

    /**
     * Create pieces and store them as faces/layers. Three sets of layers are maintained
     * corresponding to each dimension (m*axisFaceList). A piece can have anywhere from
     * one to six squares (in a 1x1x1 cubeOriginal).
     *
     * To avoid creating duplicate pieces, we store all pieces in a separate list and search it
     * for any existing pieces with the given square before creating a new one.
     *
     * The order of pieces is used in solutions and should not be changed. The outer layers follow
     * the same order as the corresponding face.
     * */
    private void createFaces() {
        mAllFaces[FACE_FRONT] = mFrontSquares;
        mAllFaces[FACE_RIGHT] = mRightSquares;
        mAllFaces[FACE_BACK] = mBackSquares;
        mAllFaces[FACE_LEFT] = mLeftSquares;
        mAllFaces[FACE_TOP] = mTopSquares;
        mAllFaces[FACE_BOTTOM] = mBottomSquares;
        mAllPieces = new ArrayList<>();

        mXaxisLayers = new ArrayList<>(mSizeX);
        mYaxisLayers = new ArrayList<>(mSizeY);
        mZaxisLayers = new ArrayList<>(mSizeZ);

        ArrayList<Piece> frontFace = new ArrayList<>();
        ArrayList<Piece> rightFace = new ArrayList<>();
        ArrayList<Piece> leftFace = new ArrayList<>();
        ArrayList<Piece> topFace = new ArrayList<>();
        ArrayList<Piece> bottomFace = new ArrayList<>();
        ArrayList<Piece> backFace = new ArrayList<>();
        ArrayList<Square> squares = new ArrayList<>();

        Piece.PieceType type;

        for (int i = 0; i < mSizeY; i++) {
            for (int j = 0; j < mSizeX; j++) {
                squares.clear();
                type = getPieceType(i, j, mSizeY, mSizeX);
                squares.add(mFrontSquares.get(i * mSizeX + j));
                if (i == 0) {
                    squares.add(mTopSquares.get(mSizeX * (mSizeZ - 1) + j));
                }
                if (i == mSizeY - 1) {
                    squares.add(mBottomSquares.get(j));
                }
                if (j == 0) {
                    squares.add(mLeftSquares.get(mSizeZ * (i + 1) - 1));
                }
                if (j == mSizeX - 1) {
                    squares.add(mRightSquares.get(mSizeZ * i));
                }
                frontFace.add(createPieceWithSquares(squares, type));
            }
        }

        for (int i = 0; i < mSizeY; i++) {
            for (int j = 0; j < mSizeZ; j++) {
                squares.clear();
                type = getPieceType(i, j, mSizeY, mSizeZ);
                if (j == 0) {
                    squares.add(mFrontSquares.get((i + 1) * mSizeX - 1));
                }
                squares.add(mRightSquares.get(i * mSizeZ + j));
                if (i == 0) {
                    squares.add(mTopSquares.get((mSizeZ - j) * mSizeX - 1));
                }
                if (i == mSizeY - 1) {
                    squares.add(mBottomSquares.get((j + 1) * mSizeX - 1));
                }
                if (j == mSizeZ - 1) {
                    squares.add(mBackSquares.get(i * mSizeX));
                }
                rightFace.add(createPieceWithSquares(squares, type));
            }
        }

        for (int i = 0; i < mSizeY; i++) {
            for (int j = 0; j < mSizeZ; j++) {
                squares.clear();
                if (j == mSizeZ - 1) {
                    squares.add(mFrontSquares.get(i * mSizeX));
                }
                type = getPieceType(i, j, mSizeY, mSizeZ);
                squares.add(mLeftSquares.get(i * mSizeZ + j));
                if (i == 0) {
                    squares.add(mTopSquares.get(j * mSizeX));
                }
                if (i == mSizeY - 1) {
                    squares.add(mBottomSquares.get(mSizeX * (mSizeZ - j - 1)));
                }
                if (j == 0) {
                    squares.add(mBackSquares.get(mSizeX * (i + 1) - 1));
                }
                if (mSizeX == 1) {
                    squares.add(mRightSquares.get((i + 1) * mSizeZ - 1 - j));
                }
                leftFace.add(createPieceWithSquares(squares, type));
            }
        }

        for (int i = 0; i < mSizeZ; i++) {
            for (int j = 0; j < mSizeX; j++) {
                squares.clear();
                if (j == 0) {
                    squares.add(mLeftSquares.get(i));
                }
                if (j == mSizeX - 1) {
                    squares.add(mRightSquares.get(mSizeZ - 1 - i));
                }
                if (i == mSizeZ - 1) {
                    squares.add(mFrontSquares.get(j));
                }
                type = getPieceType(i, j, mSizeZ, mSizeX);
                squares.add(mTopSquares.get(i * mSizeX + j));
                if (i == 0) {
                    squares.add(mBackSquares.get(mSizeX - 1 - j));
                }
                topFace.add(createPieceWithSquares(squares, type));
            }
        }

        for (int i = 0; i < mSizeZ; i++) {
            for (int j = 0; j < mSizeX; j++) {
                squares.clear();
                if (i == 0) {
                    squares.add(mFrontSquares.get(mSizeX * (mSizeY - 1) + j));
                }
                if (j == 0) {
                    squares.add(mLeftSquares.get(mSizeZ * mSizeY - 1 - i));
                }
                if (j == mSizeX - 1) {
                    squares.add(mRightSquares.get(mSizeZ * (mSizeY - 1) + i));
                }
                type = getPieceType(i, j, mSizeZ, mSizeX);
                squares.add(mBottomSquares.get(i * mSizeX + j));
                if (i == mSizeZ - 1) {
                    squares.add(mBackSquares.get(mSizeX * (mSizeY - 1) + mSizeX - 1 - j));
                }
                if (mSizeY == 1) {
                    squares.add(mTopSquares.get((mSizeZ - 1 - i) * mSizeX + j));
                }
                bottomFace.add(createPieceWithSquares(squares, type));
            }
        }

        for (int i = 0; i < mSizeY; i++) {
            for (int j = 0; j < mSizeX; j++) {
                squares.clear();
                if (i == 0) {
                    squares.add(mTopSquares.get(mSizeX - 1 - j));
                }
                if (i == mSizeY - 1) {
                    squares.add(mBottomSquares.get(mSizeX * (mSizeZ - 1) + mSizeX - 1 - j));
                }
                if (j == 0) {
                    squares.add(mRightSquares.get(mSizeZ * (i + 1) - 1));
                }
                if (j == mSizeX - 1) {
                    squares.add(mLeftSquares.get(i * mSizeZ));
                }
                type = getPieceType(i, j, mSizeY, mSizeX);
                squares.add(mBackSquares.get(i * mSizeX + j));
                if (mSizeZ == 1) {
                    squares.add(mFrontSquares.get((i + 1)  * mSizeX - 1 - j));
                }
                backFace.add(createPieceWithSquares(squares, type));
            }
        }

        mXaxisLayers.add(leftFace);
        for (int i = 1; i < mSizeX - 1; i++) {
            ArrayList<Piece> pieces = new ArrayList<>();
            for (int j = 0; j < mSizeZ - 1; j++) {
                pieces.add(topFace.get(j * mSizeX + i));
            }
            for (int j = 0; j < mSizeY - 1; j++) {
                pieces.add(frontFace.get(j * mSizeX + i));
            }
            for (int j = 0; j < mSizeZ - 1; j++) {
                pieces.add(bottomFace.get(j * mSizeX + i));
            }
            for (int j = 0; j < mSizeY - 1; j++) {
                pieces.add(backFace.get(mSizeX * (mSizeY - 1 - j) + mSizeX - 1 - i));
            }
            mXaxisLayers.add(pieces);
        }
        mXaxisLayers.add(rightFace);

        mYaxisLayers.add(bottomFace);
        for (int i = 1; i < mSizeY - 1; i++) {
            ArrayList<Piece> pieces = new ArrayList<>();
            for (int j = 0; j < mSizeX - 1; j++) {
                pieces.add(frontFace.get((mSizeY - 1 - i) * mSizeX + j));
            }
            for (int j = 0; j < mSizeZ - 1; j++) {
                pieces.add(rightFace.get((mSizeY - 1 - i) * mSizeZ + j));
            }
            for (int j = 0; j < mSizeX - 1; j++) {
                pieces.add(backFace.get((mSizeY - 1 - i) * mSizeX + j));
            }
            for (int j = 0; j < mSizeZ - 1; j++) {
                pieces.add(leftFace.get((mSizeY - 1 - i) * mSizeZ + j));
            }
            mYaxisLayers.add(pieces);
        }
        mYaxisLayers.add(topFace);

        mZaxisLayers.add(backFace);
        for (int i = 1; i < mSizeZ - 1; i++) {
            ArrayList<Piece> pieces = new ArrayList<>();
            for (int j = 0; j < mSizeX - 1; j++) {
                pieces.add(topFace.get(i * mSizeX + j));
            }
            for (int j = 0; j < mSizeY - 1; j++) {
                pieces.add(rightFace.get(mSizeZ * j + mSizeZ - 1 - i));
            }
            for (int j = 0; j < mSizeX - 1; j++) {
                pieces.add(bottomFace.get((mSizeZ - 1 - i) * mSizeX + mSizeX - 1 - j));
            }
            for (int j = 0; j < mSizeY - 1; j++) {
                pieces.add(leftFace.get((mSizeY - 1 - j) * mSizeZ + i));
            }
            mZaxisLayers.add(pieces);
        }
        mZaxisLayers.add(frontFace);
        Log.w(tag, "total pieces: " + mAllPieces.size());
    }

    /**
     * Rotate the colors in the border. This is the first part of rotating a layer.
     * */
    private static void rotateRingColors(ArrayList<ArrayList<Square>> squareList, Direction dir, int size) {
        ArrayList<ArrayList<Square>> workingCopy;
        ArrayList<Integer> tempColors = new ArrayList<>(size);
        ArrayList<Square> dst;
        ArrayList<Square> src;

        if (dir == Direction.COUNTER_CLOCKWISE) {
            // input is in clockwise order
            workingCopy = squareList;
        } else {
            // reverse and rotate
            workingCopy = new ArrayList<>(size);
            for (int i = 0; i < CUBE_SIDES; i++) {
                workingCopy.add(squareList.get(CUBE_SIDES - 1 - i));
            }
        }

        src = workingCopy.get(0);
        for (int i = 0; i < size; i++) {
            tempColors.add(src.get(i).getColor());
        }

        for (int i = 0; i < CUBE_SIDES - 1; i++) {
            dst = workingCopy.get(i);
            src = workingCopy.get(i + 1);
            for (int j = 0; j < size; j++) {
                dst.get(j).setColor(src.get(j).getColor());
            }
        }

        dst = workingCopy.get(CUBE_SIDES-1);
        for (int i = 0; i < size; i++) {
            dst.get(i).setColor(tempColors.get(i));
        }
    }

    /**
     * Rotate colors of a given face. This is the second part of rotating a face.
     * This function calls itself recursively to rotate inner squares.
     *
     * We cannot use rotateMatrix functions here as we need an in-place update of colors.
     * */
    private void rotateFaceColors(ArrayList<Square> squares, Direction direction, int size) {
        ArrayList<Integer> tempColors = new ArrayList<>(size);
        if (direction == Direction.COUNTER_CLOCKWISE) {
            for (int i = 0; i < size - 1; i++) {
                tempColors.add(squares.get(i).getColor());
                squares.get(i).setColor(squares.get(i * size + size - 1).getColor());
            }

            for (int i = 0; i < size - 1; i++) {
                squares.get(i * size + size - 1).setColor(
                        squares.get(size * size - 1 - i).getColor());
            }

            for (int i = 0; i < size - 1; i++) {
                squares.get(size * size - 1 - i).setColor(
                        squares.get(size * (size - 1 - i)).getColor());
            }

            for (int i = 0; i < size - 1; i++) {
                squares.get(size * (size - 1 - i)).setColor(tempColors.get(i));
            }
        } else {
            for (int i = 0; i < size - 1; i++) {
                tempColors.add(squares.get(i).getColor());
                squares.get(i).setColor(squares.get(size * (size - 1 - i)).getColor());
            }
            for (int i = 0; i < size - 1; i++) {
                squares.get(size * (size - 1 - i)).setColor(
                        squares.get(size * size - 1 - i).getColor());
            }
            for (int i = 0; i < size - 1; i++) {
                squares.get(size * size - 1 - i).setColor(
                        squares.get(i * size + size - 1).getColor());
            }
            for (int i = 0; i < size - 1; i++) {
                squares.get(i * size + size - 1).setColor(tempColors.get(i));
            }
        }

        if (size > 3) {
            ArrayList<Square> subset = new ArrayList<>(size - 2);
            for (int i = 1; i < size - 1; i++) {
                for (int j = 1; j < size - 1; j++) {
                    subset.add(squares.get(i * size + j));
                }
            }
            rotateFaceColors(subset, direction, size - 2);
        }
    }

    public float getFrontFaceZ() {
        return (squareSize + GAP) * (mSizeZ / 2.0f);
    }

    public float getBackFaceZ() {
        return - (squareSize + GAP) * (mSizeZ / 2.0f);
    }

    public float getLeftFaceX() {
        return  - (squareSize + GAP) * (mSizeX / 2.0f);
    }

    public float getRightFaceX() {
        return (squareSize + GAP) * (mSizeX / 2.0f);
    }

    public float getTopFaceY() {
        return (squareSize + GAP) * (mSizeY / 2.0f);
    }

    public float getBottomFaceY() {
        return - (squareSize + GAP) * (mSizeY / 2.0f);
    }


    public static Axis face2axis(int face) {
        switch (face) {
            case FACE_BACK:
            case FACE_FRONT:
                return Axis.Z_AXIS;
            case FACE_BOTTOM:
            case FACE_TOP:
                return Axis.Y_AXIS;
            case FACE_LEFT:
            case FACE_RIGHT:
                return Axis.X_AXIS;
            default:
                throw new InvalidParameterException("Whats on face " + face);
        }
    }

    public static String faceName(int face) {
        return faceNames[face];
    }

    protected int getAxisSize(Axis axis) {
        switch (axis) {
            case X_AXIS: return mSizeX;
            case Y_AXIS: return mSizeY;
            case Z_AXIS: return mSizeZ;
            default: throw new InvalidParameterException();
        }
    }

    /**
     * Rotate the face specified by @face and @axis.
     *
     * Note that the direction is relative to positive direction of the mentioned axis, and not
     * the visible side of face. This is against the normal cubeOriginal notation where direction is
     * usually mentioned relative to the face being rotated.
     *
     * For instance, in traditional cubeOriginal notation, L stands for left face clockwise where the
     * clock is running on the visible face of the left face. It will rotate left face clockwise
     * around the negative x axis. In our case, left face is face-0 on X axis and to achieve "L",
     * we should call this function with values (X, CCW, 0)
     *
     * Example mappings from traditional notation to this function (assuming 3x3x3 cubeOriginal):
     * Front face clockwise: (Z, CW, 2)
     * Left face clockwise: (X, CCW, 0)
     * Bottom face clockwise: (Y, CCW, 0)
     * */
    protected void rotate(Axis axis, Direction direction, int face) {
        int maxSize = getAxisSize(axis);
        if (face >= maxSize) {
            throw new AssertionError(String.format("face mismsatch %d %d %d: axis %s, face %d",
                    mSizeX, mSizeY, mSizeZ, axis.toString(), face));
        }

        int w = 0, h = 0;

        // The face to be rotated (in case we are rotating an edge layer.
        ArrayList<Square> faceSquares = null;

        // Additional face to be rotated if the dimension along the axis of rotation is 1
        ArrayList<Square> oppositeFace = null;

        // This list holds the squares from the sides of the layer being rotated
        ArrayList<ArrayList<Square>> squareList = new ArrayList<>(CUBE_SIDES);

        for (int i = 0; i < CUBE_SIDES; i++) {
            squareList.add(new ArrayList<Square>());
        }
        switch (axis) {
            case X_AXIS:
                for (int i = 0; i < mSizeY; i++) {
                    squareList.get(0).add(mFrontSquares.get(mSizeX * i + face));
                    squareList.get(2).add(mBackSquares.get((mSizeY - 1 - i) * mSizeX +
                            (mSizeX - 1 - face)));
                }
                for (int i = 0; i < mSizeZ; i++) {
                    squareList.get(1).add(mTopSquares.get(mSizeX * i + face));
                    squareList.get(3).add(mBottomSquares.get(mSizeX * i + face));
                }

                if (face == 0) {
                    faceSquares = mLeftSquares;
                } else if (face == mSizeX - 1) {
                    faceSquares = mRightSquares;
                }
                if (mSizeX == 1)
                    oppositeFace = mRightSquares;
                w = mSizeZ;
                h = mSizeY;
                break;

            case Y_AXIS:
                for (int i = 0; i < mSizeX; i++) {
                    squareList.get(0).add(
                            mFrontSquares.get((mSizeY - 1 - face) * mSizeX + i));
                    squareList.get(2).add(
                            mBackSquares.get((mSizeY - 1 - face) * mSizeX + i));
                }
                for (int i = 0; i < mSizeZ; i++) {
                    squareList.get(1).add(
                            mLeftSquares.get((mSizeY - 1 - face) * mSizeZ + i));
                    squareList.get(3).add(
                            mRightSquares.get((mSizeY - 1 - face) * mSizeZ + i));
                }

                if (face == 0) {
                    faceSquares = mBottomSquares;
                } else if (face == mSizeY - 1) {
                    faceSquares = mTopSquares;
                }
                if (mSizeY == 1)
                    oppositeFace = mTopSquares;
                w = mSizeX;
                h = mSizeZ;
                break;

            case Z_AXIS:
                for (int i = 0; i < mSizeX; i++) {
                    squareList.get(0).add(mTopSquares.get(mSizeX * face + i));
                    squareList.get(2).add(mBottomSquares.get(
                            mSizeX * (mSizeZ - 1 - face) + mSizeX - 1 - i));
                }
                for (int i = 0; i < mSizeY; i++) {
                    squareList.get(1).add(
                            mRightSquares.get(mSizeZ * i + mSizeZ - 1 - face));
                    squareList.get(3).add(
                            mLeftSquares.get(mSizeZ * (mSizeY - 1 - i) + face));
                }

                if (face == 0) {
                    faceSquares = mBackSquares;
                } else if (face == mSizeZ - 1) {
                    faceSquares = mFrontSquares;
                }
                if (mSizeZ == 1)
                    oppositeFace = mFrontSquares;
                w = mSizeX;
                h = mSizeY;
                break;
        }

        boolean symmetric = isSymmetricAroundAxis(axis);
        if (symmetric) {
            int size = axis == Axis.X_AXIS ? mSizeY : mSizeX;
            rotateRingColors(squareList, direction, size);

            if (faceSquares != null) {
                if (face == 0) {
                    /**
                     * Lower layers store colors in opposite direction, and needs to be rotated
                     * in the opposite direction
                     * */
                    rotateFaceColors(faceSquares,
                            direction == Direction.CLOCKWISE ?
                                    Direction.COUNTER_CLOCKWISE : Direction.CLOCKWISE, size);
                } else {
                    // Rotate a face that is on the positive edge of the
                    // corresponding axis (front, top or right).
                    // As squares are stored in clockwise order, rotation is straightforward.
                    rotateFaceColors(faceSquares, direction, size);
                }
            }

            /**
             * "Opposite face" will be always on the positive edge of the axis
             * */
            if (oppositeFace != null) {
                rotateFaceColors(oppositeFace, direction, size);
            }
        } else {
            /**
             * If not symmetric, rotate 180' along the given axis
             * */
            skewedRotateRingColors(squareList);
            if (faceSquares != null)
                skewedRotateFaceColors(faceSquares, w, h);
            if (oppositeFace != null)
                skewedRotateFaceColors(oppositeFace, w, h);
        }
    }

    private static void skewedRotateFaceColors(ArrayList<Square> squares, int w, int h) {
        if (w == 1 || h == 1) {
            int len = Math.max(w, h);
            for (int i = 0; i < len / 2; i++) {
                Square src = squares.get(i);
                Square dst = squares.get(len - 1 - i);
                int color = src.getColor();
                src.setColor(dst.getColor());
                dst.setColor(color);
            }
            return;
        }

        for (int i = 0; i < w - 1; i++) {
            Square src = squares.get(i);
            Square dst = squares.get(w*h - 1 - i);
            int color = src.getColor();
            src.setColor(dst.getColor());
            dst.setColor(color);
        }
        for (int i = 1; i < h; i++) {
            Square src = squares.get(i*w);
            Square dst = squares.get(w* (h-i) - 1);
            int color = src.getColor();
            src.setColor(dst.getColor());
            dst.setColor(color);
        }
        if (w + h <= 6 || w < 3 || h < 3) return;
        ArrayList<Square> subset = new ArrayList<>();
        for (int i = 1; i < w - 1; i++) {
            for (int j = 1; j < h -1; j++) {
                subset.add(squares.get(j*w + i));
            }
        }
        skewedRotateFaceColors(subset, w-2, h-2);
    }

    private static void skewedRotateRingColors(ArrayList<ArrayList<Square>> squareList) {
        ArrayList<ArrayList<Square>> workingCopy;
        ArrayList<Integer> tempColors = new ArrayList<>();
        ArrayList<Square> dst;
        ArrayList<Square> src;

        src = squareList.get(0);
        dst = squareList.get(2);
        for (int i = 0; i < src.size(); i++) {
            int color = src.get(i).getColor();
            src.get(i).setColor(dst.get(i).getColor());
            dst.get(i).setColor(color);
        }

        src = squareList.get(1);
        dst = squareList.get(3);
        for (int i = 0; i < src.size(); i++) {
            int color = src.get(i).getColor();
            src.get(i).setColor(dst.get(i).getColor());
            dst.get(i).setColor(color);
        }
    }

    public int getSizeX() {
        return mSizeX;
    }

    public int getSizeY() {
        return mSizeY;
    }

    public int getSizeZ() {
        return mSizeZ;
    }

    public float getSquareSize() {
        return squareSize;
    }

    protected boolean isSymmetricAroundAxis(Axis axis) {
        switch (axis) {
            case X_AXIS:
                return mSizeY == mSizeZ;
            case Y_AXIS:
                return mSizeX == mSizeZ;
            case Z_AXIS:
                return mSizeX == mSizeY;
        }
        throw new InvalidParameterException();
    }

    /**
     * Rotate the whole cubeOriginal along the given axis.
     * Can be used for 90' rotations in skewed cubes
     *
     * This function basically reorganizes the cubeOriginal
     * */
    protected void rotate(Axis axis, Direction direction) {
        int x = 0, y = 0, z = 0;
        int count = 1;
        int angle = -90;
        if (direction == Direction.COUNTER_CLOCKWISE) {
            angle = 90;
            // TOO lazy to write reverse functions; just rotate thrice for CCW
            count = 3;
        }

        for (int i = 0; i < count; i++) {
            switch (axis) {
                case X_AXIS:
                    x = 1;
                    rotateCubeX();
                    break;

                case Y_AXIS:
                    y = 1;
                    rotateCubeY();
                    break;

                case Z_AXIS:
                    z = 1;
                    rotateCubeZ();
                    break;
            }
        }
        createFaces();
        updateSquareFaces();

        for (Square sq: mAllSquares) {
            sq.rotateCoordinates(x, y, z, angle);
        }
    }

    private static <T> ArrayList<T> rotateMatrix(ArrayList<T> matrix, int w, int h) {
        ArrayList<T> rotatedMatrix = new ArrayList<>();
        for (int i = 0; i < w; i++) {
            for (int j = h; j > 0; j--) {
                rotatedMatrix.add(matrix.get((j - 1) * w + i));
            }
        }
        return rotatedMatrix;
    }

    private static <T> ArrayList<T> rotateMatrixCCW(ArrayList<T> matrix, int w, int h) {
        ArrayList<T> rotatedMatrix = new ArrayList<>();
        for (int i = w - 1; i >= 0; i--) {
            for (int j = 0; j < h; j++) {
                rotatedMatrix.add(matrix.get(j * w + i));
            }
        }
        return rotatedMatrix;
    }

    private void debugfacesizes() {
        Log.w(tag, String.format("%d-%d-%d Front %d, Right %d, Back %d, Left %d, Top %d, Bottom %d",
                mSizeX, mSizeY, mSizeZ,
                mFrontSquares.size(),
                mRightSquares.size(),
                mBackSquares.size(),
                mLeftSquares.size(),
                mTopSquares.size(),
                mBottomSquares.size()
                ));
    }

    protected void rotateCubeX() {
        ArrayList<Square> tempFace = new ArrayList<>(mTopSquares);
        mTopSquares = mFrontSquares;
        mFrontSquares = mBottomSquares;
        mBottomSquares = new ArrayList<>();
        for (int i = mSizeY - 1; i >= 0; i--) {
            for (int j = mSizeX - 1; j >= 0; j--) {
                mBottomSquares.add(mBackSquares.get(i*mSizeX + j));
            }
        }
        mBackSquares.clear();
        for (int i = mSizeZ - 1; i >= 0; i--) {
            for (int j = mSizeX - 1; j >= 0; j--) {
                mBackSquares.add(tempFace.get(i*mSizeX + j));
            }
        }


        mRightSquares = rotateMatrix(mRightSquares, mSizeZ, mSizeY);
        mLeftSquares = rotateMatrixCCW(mLeftSquares, mSizeZ, mSizeY);

        int temp = mSizeY;
        mSizeY = mSizeZ;
        mSizeZ = temp;
    }

    protected void rotateCubeY() {
        ArrayList<Square> tempFace = mFrontSquares;
        mFrontSquares = mRightSquares;
        mRightSquares = mBackSquares;
        mBackSquares = mLeftSquares;
        mLeftSquares = tempFace;
        mTopSquares = rotateMatrix(mTopSquares, mSizeX, mSizeZ);
        mBottomSquares = rotateMatrixCCW(mBottomSquares, mSizeX, mSizeZ);

        int temp = mSizeX;
        mSizeX = mSizeZ;
        mSizeZ = temp;
    }

    protected void rotateCubeZ() {
        ArrayList<Square> tempFace = new ArrayList<>(mTopSquares);
        mTopSquares = rotateMatrix(mLeftSquares, mSizeZ, mSizeY);
        mLeftSquares = rotateMatrix(mBottomSquares, mSizeX, mSizeZ);
        mBottomSquares = rotateMatrix(mRightSquares, mSizeZ, mSizeY);
        mRightSquares = rotateMatrix(tempFace, mSizeX, mSizeZ);
        mFrontSquares = rotateMatrix(mFrontSquares, mSizeX, mSizeY);
        mBackSquares = rotateMatrixCCW(mBackSquares, mSizeX, mSizeY);

        int temp = mSizeY;
        mSizeY = mSizeX;
        mSizeX = temp;
    }

    private void updateSquareFaces() {
        for (int i = 0; i < FACE_COUNT; i++) {
            ArrayList<Square> face = mAllFaces[i];
            for (Square sq: face) {
                sq.setFace(i);
            }
        }
    }

    public static int[] getOrderedFaces(Axis axis) {
        if (axis == Axis.X_AXIS) return orderedFacesXaxis;
        else if (axis == Axis.Y_AXIS) return orderedFacesYaxis;
        return orderedFacesZaxis;
    }
}
