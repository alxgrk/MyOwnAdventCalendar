package com.alxgrk.myownadventcalendar.animation;

import android.os.Handler;
import android.os.Looper;

import com.alxgrk.myownadventcalendar.views.DoorView;
import com.alxgrk.myownadventcalendar.views.ZoomableViewGroup;

import java.util.TreeSet;

public class ZoomToDoorAnimation {

    private ZoomableViewGroup zoomView;

    private TreeSet<DoorView> currentDoors;

    public ZoomToDoorAnimation(ZoomableViewGroup zoomView) {
        this.zoomView = zoomView;
        this.currentDoors = new TreeSet<>();
    }

    public void start(final TreeSet<DoorView> doorsToOpen) {
        zoomView.blockUser();

        currentDoors = doorsToOpen;

        DoorView firstDoor = doorsToOpen.pollFirst();
        if (null != firstDoor)
            runAnimation(firstDoor);
    }

    private void runAnimation(final DoorView doorToOpen) {

        final Runnable taskAfterDoorOpened = new Runnable() {
            @Override
            public void run() {
                final DoorView nextDoor = currentDoors.pollFirst();
                if (null != nextDoor) {
                    new Handler(Looper.getMainLooper())
                            .postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    runAnimation(nextDoor);
                                }
                            }, 3000);
                } else {
                    zoomView.unblockUser();
                }
            }
        };

        final Runnable taskOpenDoor = new Runnable() {
            @Override
            public void run() {
                doorToOpen.animateOpenDoor(zoomView, taskAfterDoorOpened);
            }
        };

        final Runnable taskZoomToDoor = new Runnable() {
            @Override
            public void run() {
                zoomView.zoomToDoor(doorToOpen, zoomView.createZoomEndListener(taskOpenDoor));
            }
        };

        zoomView.zoomToInit(zoomView.createZoomEndListener(taskZoomToDoor));
    }
}
