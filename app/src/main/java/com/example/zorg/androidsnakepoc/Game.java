package com.example.zorg.androidsnakepoc;

/**
 * Created by roy on 26-Dec-14.
 */
import com.example.zorg.androidsnakepoc.SnakeApp;
import java.util.ArrayList;
import java.util.Random;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.TextView;

public class Game extends View {

    private boolean setupComplete = false;
    private int pxSquare, sqBorder = 0, padding = 0;
    private static int squaresWidth, squaresHeight;
    private ArrayList<Block> walls;
    public Snake snake;
    private Food food;
    private Random random;
    private TextView scoreView;
    private SnakeScreen mActivity;
    public boolean gameOver = false;
    private int score;
    private long delay;
    public static MediaPlayer mpEat;
    public static int reasonOfDeath;

    public Game(Context context) {
        super(context);
    }

    public Game(Context context, SnakeScreen activity, TextView scoreView,
                GameAnalytics gameAnal) {
        super(context);

        reasonOfDeath = -1;
        mActivity = activity;
        random = new Random();
        this.scoreView = scoreView;
        mpEat = MediaPlayer.create(context, R.raw.eating);
        delay = SnakeApp.getDelay();

    }

    // If User Scores
    private void score() {
        score++;
        scoreView.setText(Integer.toString(this.score));
    }

    public int getScore() {
        return score;
    }

    public int getReasonOfDeath() {
        return reasonOfDeath;
    }

    // Draw View
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        if (!setupComplete) {
            setup();
            this.invalidate();
            return;
        }

        // Draw Walls
        for (Block block : walls) {
            block.draw(canvas);
        }

        // Move & Draw Snake
        snake.draw(canvas);

        // Draw Food
        food.draw(canvas);

        final View parent = this;
        if (!snake.stopped) {
            new Thread(new Runnable() {
                public void run() {
                    parent.postDelayed(new Runnable() {
                        public void run() {
                            parent.invalidate();
                        }
                    }, delay);
                }
            }).start();
        } else if (gameOver) {
            new Thread(new Runnable() {
                public void run() {
                    parent.postDelayed(new Runnable() {
                        public void run() {
                            mActivity.gameOver();
                        }
                    }, 100);
                }
            }).start();
        }
    }

    // Setup View
    public void setup() {

        // reseting reason of death
        reasonOfDeath = -1;

        // Reset Score
        score = -1;
        this.score();
        gameOver = false;

        // Calculate Width of View in Inches
        int pxWidth = getWidth();
        int pxHeight = getHeight();
        int dpi = getResources().getDisplayMetrics().densityDpi;
        float inWidth = ((float) pxWidth) / dpi;
        float inHeight = ((float) pxHeight) / dpi;

        // Calculate Number of Squares Based on View Size (Minimum 15 x 15)
        squaresWidth = (int) (inWidth * 10.0);
        squaresHeight = (int) (inHeight * 10.0);
        if (squaresHeight < 15)
            squaresHeight = 15;
        if (squaresWidth < 15)
            squaresWidth = 15;

        // Calculate Size of Squares
        int pxSquareWidth = pxWidth / squaresWidth;
        int pxSquareHeight = pxHeight / squaresHeight;
        if (pxSquareWidth > pxSquareHeight)
            pxSquare = pxSquareHeight; // Extra Space on Sides
        else
            pxSquare = pxSquareWidth; // Extra Space on Top

        // Calculate Padding Around & Between Squares
        padding = (pxWidth - squaresWidth * pxSquare) / 2;

        // Build List of Wall Objects
        walls = new ArrayList<Block>();
        for (int j = 0; j < squaresWidth; j++) {
            walls.add(new Block(j, 0, 0)); // Top Walls
            walls.add(new Block(j, squaresHeight - 1, 0)); // Bottom Walls
        }
        for (int j = 1; j < (squaresHeight - 1); j++) { // Left Walls
            walls.add(new Block(0, j, 0)); // Left Walls
            walls.add(new Block(squaresWidth - 1, j, 0)); // Right Walls
        }

        // Create Snake
        snake = new Snake();

        // Create Food
        food = new Food(snake, walls);

        setupComplete = true;
    }

    public static void setReasonOfDeath(int reason) {
        reasonOfDeath = reason;
    }

    // Snake Object contains a list of blocks, knows if it is moving and
    // which direction it is moving
    public class Snake {

        public ArrayList<Block> blocks;
        private int direction, length;
        public boolean stopped = false;

        // Create Snake with 3 Blocks
        public Snake() {

            // Create Leading Block
            blocks = new ArrayList<Block>();
            blocks.add(new Block(squaresWidth / 2, squaresHeight / 2, 1));
            length = 3;

            // Calculate Random Initial Direction and Add 2 Remaining Blocks
            direction = random.nextInt(4);
            switch (direction) {
                case 0: // Going Right
                    blocks.add(new Block(squaresWidth / 2 - 1, squaresHeight / 2, 1));
                    blocks.add(new Block(squaresWidth / 2 - 2, squaresHeight / 2, 1));
                    break;
                case 1: // Going Down
                    blocks.add(new Block(squaresWidth / 2, squaresHeight / 2 - 1, 1));
                    blocks.add(new Block(squaresWidth / 2, squaresHeight / 2 - 2, 1));
                    break;
                case 2: // Going Left
                    blocks.add(new Block(squaresWidth / 2 + 1, squaresHeight / 2, 1));
                    blocks.add(new Block(squaresWidth / 2 + 2, squaresHeight / 2, 1));
                    break;
                case 3: // Going Up
                    blocks.add(new Block(squaresWidth / 2, squaresHeight / 2 + 1, 1));
                    blocks.add(new Block(squaresWidth / 2, squaresHeight / 2 + 2, 1));
            }
        }

        // Move & Draw Snake
        public void draw(Canvas canvas) {
            if (!stopped)
                move();
            for (Block block : blocks)
                block.draw(canvas);
        }

        public void turnLeft() {
            if (this.direction != 0 && this.direction != 2)
                this.direction = 2;
        }

        public void turnRight() {
            if (this.direction != 0 && this.direction != 2)
                this.direction = 0;
        }

        // If Not Going Down or Up, Go Down (Four Direction Only)
        public void turnDown() {
            if (this.direction != 1 && this.direction != 3)
                this.direction = 1;
        }

        // If Not Going Down or Up, Go Up (Four Direction Only)
        public void turnUp() {
            if (this.direction != 1 && this.direction != 3)
                this.direction = 3;
        }

        // Move Snake 1 Space in Current Direction
        public void move() {

            // Grab Current Front Block
            Block frontBlock = blocks.get(0);

            // Create New Block at Front of Snake
            Block newBlock;
            switch (direction) {
                // moving right
                case 0:
                    newBlock = new Block(frontBlock.x + 1, frontBlock.y, 1);
                    break;
                // moving down
                case 1:
                    newBlock = new Block(frontBlock.x, frontBlock.y + 1, 1);
                    break;
                // moving left
                case 2:
                    newBlock = new Block(frontBlock.x - 1, frontBlock.y, 1);
                    break;
                // moving up
                default:
                    newBlock = new Block(frontBlock.x, frontBlock.y - 1, 1);
            }

            // snake hits a wall
            if (this.collides(newBlock) || newBlock.collides(walls)) {
                stopped = true;
                for (Block block : blocks) {
                    block.setType(3);
                }
                newBlock.setType(0);
                gameOver = true;

                // If New Block is Clear
            } else {

                // Add New Block to the Front
                blocks.add(0, newBlock);

                // If Collision with Food
                if (this.collides(food)) {
                    food.move(this, walls);
                    length++;
                    mpEat.start();
                    score();

                    // If No Collision with Food, Remove Last Block
                } else
                    blocks.remove(length);
            }
        }

        // Check for Collisions with a Block
        public boolean collides(Block block) {
            for (Block oneBlock : this.blocks)
                if (block.collides(oneBlock))
                    return true;
            return false;
        }
    }

    public class Block {
        public int x = 0, y = 0;
        ShapeDrawable shape;
        private int type = -1;

        public Block() {
        }

        public Block(int x, int y, int type) {
            this.x = x;
            this.y = y;

            shape = new ShapeDrawable(new RectShape());
            shape.setBounds(padding + x * pxSquare + sqBorder, padding + y
                    * pxSquare + sqBorder, padding + (x + 1) * pxSquare
                    - sqBorder, padding + (y + 1) * pxSquare - sqBorder);

            this.setType(type);
        }

        public void draw(Canvas canvas) {
            shape.draw(canvas);
        }

        public boolean collides(Block block) {

            int reason = -1;

            if (block.x == this.x && block.y == this.y) {
                // if block is not block
                if (block.type == 0) {
                    if (block.x == 0)
                        // collision with left wall
                        reason = 0;
                    else if (block.x == Game.squaresWidth - 1)
                        // collision with right wall if equals to (squaresWidth
                        // - 1)
                        reason = 1;
                    else if (block.y == 0)
                        // collision with top wall
                        reason = 2;
                    else if (block.y == Game.squaresHeight - 1)
                        // collision with bottom wall if equals to
                        // (squaresHeight
                        // -1)
                        reason = 3;
                    else
                        // collision with self
                        reason = 4;

                    // setting game reason of death
                    Game.setReasonOfDeath(reason);
                }
                return true;
            }

            return false;
        }

        public boolean collides(ArrayList<Block> blocks) {
            for (Block block : blocks) {
                if (this.collides(block))
                    return true;
            }
            return false;
        }

        public void setType(int type) {
            switch (type) {
                case 0: // If Wall, Paint Black
                    shape.getPaint().setColor(Color.BLACK);
                    this.type = 0;
                    break;
                case 1: // If Snake, Paint Blue
                    shape.getPaint().setColor(Color.parseColor("#ff33b5e5"));
                    this.type = 1;
                    break;
                case 2: // If Food, Paint Grey
                    shape.getPaint().setColor(Color.GRAY);
                    this.type = 2;
                    break;
                case 3: // If Collision, Paint Red
                    shape.getPaint().setColor(Color.RED);
                    this.type = 3;
            }
        }
    }

    class Food extends Block {

        public Food(Snake snake, ArrayList<Block> blocks) {
            shape = new ShapeDrawable(new RectShape());
            this.setType(2);
            this.move(snake, blocks);
        }

        public void move(Snake snake, ArrayList<Block> blocks) {
            while (true) {
                this.x = random.nextInt(squaresWidth - 3) + 1;
                this.y = random.nextInt(squaresHeight - 3) + 1;
                if (!snake.collides(this) && !this.collides(blocks))
                    break;
            }
            shape.setBounds(padding + x * pxSquare + sqBorder, padding + y
                    * pxSquare + sqBorder, padding + (x + 1) * pxSquare
                    - sqBorder, padding + (y + 1) * pxSquare - sqBorder);
        }
    }
}

