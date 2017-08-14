package com.example.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.MediaPlayer;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Reloj de 24 segundos que soporta seteo de padding por XML
 */
public class PaddingClockView extends View implements IClockView{

    protected Paint backgroundPaint;
    protected Paint squarePaint;
    protected Paint gridPaint;
    protected Paint activeTextPaint;
    protected Paint inactiveTextPaint;
    protected int cellPadding = 0;

    protected boolean showGridBackground = true;
    protected boolean showGrid = false;

    private int currentNumber = 24;
    private float canvasSize;
    private float cellSize;
    private float nColumns;
    private float horizontalOffset;
    private float verticalOffset;

    /**
     * Se utiliza cuando se crean vistas manualmente, por código
     * @param context contexto en el cual se infla vista
     */
    public PaddingClockView(Context context) {
        this(context,null);
    }

    /**
     * Se utiliza cuando se crea la vista desde XML.
     * @param context
     * @param attrs
     */
    public PaddingClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    /**
     *
     */
    private void init(){
        backgroundPaint = createPaintFromResource(R.color.colorPrimary);
        squarePaint = createPaintFromResource(R.color.green);
        gridPaint = createPaintFromResource(R.color.blue);
        activeTextPaint = createPaintFromResource(R.color.colorAccent);
        inactiveTextPaint = createPaintFromResource(R.color.colorInactive);

        if(!isInEditMode())
            start();
    }

    private Paint createPaintFromResource(@ColorRes int color){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(ContextCompat.getColor(getContext(),color));
        return paint;
    }


    //region //View overrides

    @Override
    protected void onDraw(Canvas canvas) {
        final int canvasWidth = canvas.getWidth();
        final int canvasHeight = canvas.getHeight();

        setupGrid(canvasWidth,canvasHeight);

        List<ClockPath> paths = initNumberPaths();

        updatePathsStatesForNumber(paths, currentNumber);

        canvas.drawRect(0,0,canvasWidth,canvasHeight,backgroundPaint);
        if(showGridBackground) {
            canvas.drawRect(
                    horizontalOffset,
                    verticalOffset,
                    canvasSize + horizontalOffset,
                    canvasSize + verticalOffset,
                    squarePaint);
        }

        if(showGrid)
            paintGrid(canvas);

        for(ClockPath clockPath : paths){
            Paint pathPaint = clockPath.isActive ? activeTextPaint : inactiveTextPaint;
            canvas.drawPath(clockPath.path,pathPaint);
        }
    }

    /**
     * Setea los valores necesarios para dibujar los numeros, en base al espacio disponible para dibujar
     * @param availableWidth ancho disponible para dibujar
     * @param availableHeight alto disponible para dibujar
     */
    private void setupGrid(float availableWidth, float availableHeight){
        canvasSize = Math.min(
                availableWidth - getPaddingLeft() - getPaddingRight(),
                availableHeight - getPaddingTop() - getPaddingTop());
        if(canvasSize < 0)
            canvasSize = 0;
        nColumns = 18 + (cellPadding * 2);
        cellSize = canvasSize / nColumns * 1.0f;
        horizontalOffset = getPaddingLeft();
        verticalOffset = getPaddingTop();
    }

    private void paintGrid(Canvas canvas){
        float w = getPaddingLeft(), h = getPaddingTop();
        while (w < canvasSize + horizontalOffset){
            canvas.drawLine(w,verticalOffset,w,canvasSize + verticalOffset, gridPaint);
            w+= cellSize;
        }
        while (h < canvasSize + verticalOffset){
            canvas.drawLine(horizontalOffset,h,canvasSize + horizontalOffset,h, gridPaint);
            h+= cellSize;
        }
    }

    private class ClockPath{
        Path path;
        boolean isActive;

        ClockPath(Path path, boolean isActive) {
            this.path = path;
            this.isActive = isActive;
        }
    }

    private class GridPoint{
        int x, y;

        GridPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Create paths that when together, allow us to represent a number
     */
    private @NonNull List<ClockPath> initNumberPaths(){
        List<ClockPath> paths = new ArrayList<>();
        int nDigits = 2;
        for(int i=0; i < nDigits; i++){
            int column = i * 10;
            //A
            paths.add(createPath(column,cellSize,
                    new GridPoint(0,3),
                    new GridPoint(8,3),
                    new GridPoint(6,5),
                    new GridPoint(2,5)));
            //B
            paths.add(createPath(column,cellSize,
                    new GridPoint(0,5),
                    new GridPoint(0,9),
                    new GridPoint(2,8),
                    new GridPoint(2,5)));
            //C
            paths.add(createPath(column,cellSize,
                    new GridPoint(8,3),
                    new GridPoint(8,9),
                    new GridPoint(6,8),
                    new GridPoint(6,5)));
            //D
            paths.add(createPath(column,cellSize,
                    new GridPoint(0,9),
                    new GridPoint(2,8),
                    new GridPoint(6,8),
                    new GridPoint(8,9),
                    new GridPoint(6,10),
                    new GridPoint(2,10)));
            //E
            paths.add(createPath(column,cellSize,
                    new GridPoint(0,9),
                    new GridPoint(0,15),
                    new GridPoint(2,13),
                    new GridPoint(2,10)));
            //F
            paths.add(createPath(column,cellSize,
                    new GridPoint(8,9),
                    new GridPoint(8,15),
                    new GridPoint(6,13),
                    new GridPoint(6,10)));
            //G
            paths.add(createPath(column,cellSize,
                    new GridPoint(8,15),
                    new GridPoint(0,15),
                    new GridPoint(2,13),
                    new GridPoint(6,13)));
        }

        return paths;
    }

    /**
     * Crear un path con uno o mas puntos
     * @param startingColumn columna en la que se comienza a dibujar el path
     * @param cellSize el tamaño de una celda de la grilla
     * @param startingPoint punto en el que se comienza a dibujar el path
     * @param nextPoints los siguientes puntos sobre los cuales se dibujara el path
     */
    private ClockPath createPath(int startingColumn,
                                 float cellSize,
                                 GridPoint startingPoint,
                                 GridPoint... nextPoints){
        float offset = startingColumn * cellSize;
        Path path = new Path();
        path.moveTo(offset + (startingPoint.x + cellPadding) * cellSize + horizontalOffset,
                (startingPoint.y + cellPadding) * cellSize + verticalOffset);

        for(GridPoint point : nextPoints){
            path.lineTo(offset + (point.x + cellPadding) * cellSize + horizontalOffset,
                    (point.y + cellPadding)  * cellSize + verticalOffset);
        }
        return new ClockPath(path,false);
    }


    private final static int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5, G = 6;
    /**
     * Actualiza el estado de cada path para que muestre el numero que se quiere mostrar
     * @param paths sobre los cuales se aplicará el cambio de estado
     * @param number Numero que se quiere mostrar
     */
    private void updatePathsStatesForNumber(List<ClockPath> paths, int number){
        if(number < 0 || number > 24)
            return;

        for (ClockPath path : paths){
            path.isActive = false;
        }

        int digitIndex = 0;
        do{
            int digit = number % 10;
            number /= 10;

            switch (digit){
                case 0:
                    updatePathsForSingleDigit(paths,digitIndex,A,B,C,E,F,G);
                    break;
                case 1:
                    updatePathsForSingleDigit(paths,digitIndex,C,F);
                    break;
                case 2:
                    updatePathsForSingleDigit(paths,digitIndex,A,C,D,E,G);
                    break;
                case 3:
                    updatePathsForSingleDigit(paths,digitIndex,A,C,D,F,G);
                    break;
                case 4:
                    updatePathsForSingleDigit(paths,digitIndex,B,D,C,F);
                    break;
                case 5:
                    updatePathsForSingleDigit(paths,digitIndex,A,B,D,F,G);
                    break;
                case 6:
                    updatePathsForSingleDigit(paths,digitIndex,A,B,D,F,G,E);
                    break;
                case 7:
                    updatePathsForSingleDigit(paths,digitIndex,A,C,F);
                    break;
                case 8:
                    updatePathsForSingleDigit(paths,digitIndex,A,B,C,D,E,F,G);
                    break;
                case 9:
                    updatePathsForSingleDigit(paths,digitIndex,A,B,C,D,F);
                    break;
                default:
                    updatePathsForSingleDigit(paths,digitIndex);
            }
            digitIndex++;
        }while (number > 0);

    }

    /**
     * Actualiza el estado de cada path correspondiente a un solo digito. Asume que
     * cada digito está conformado por 7 paths
     * @param paths
     * @param digitIndex Numero de digito empezando de la derecha. Por ejemplo, si el numero es "123",
     *                   3 -> digitIndex: 0
     *                   2 -> digitIndex: 1
     *                   1 -> digitIndex: 2
     * @param activeIndexes Indice de los paths que deberian estar activos
     */
    private void updatePathsForSingleDigit(List<ClockPath> paths, int digitIndex, int... activeIndexes){
        //O(m * n)
        int offset = paths.size() - (digitIndex + 1) * 7;
        if(offset < 0)
            return;
        for(int i=0; i < 7; i++){
            ClockPath path = paths.get(offset + i);
            for(int active : activeIndexes){
                if(i == active){
                    path.isActive = true;
                    break;
                }
                path.isActive = false;
            }
        }
    }

    //endregion

    //region //Timer manager

    @Override
    public void start() {
        updateClock();
    }

    @Override
    public void stop() {
        removeCallbacks(updateRunnable);
    }

    @Override
    public void reset() {
        currentNumber = 0;
    }

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            updateClock();
        }
    };

    private void updateClock() {
        if(currentNumber > 0) {
            currentNumber--;
            invalidate();

            if(currentNumber != 0) {
                //sigue actualizando
                postDelayed(updateRunnable, 1000L);
            }else{
                //suena la vocina
                MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.buzzer);
                mp.start();
            }
        }
    }

    //endregion

}
