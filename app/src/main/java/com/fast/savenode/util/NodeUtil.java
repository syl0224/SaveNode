package com.fast.savenode.util;

import android.app.UiAutomation;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.display.DisplayManagerGlobal;
import android.view.Display;
import android.view.accessibility.AccessibilityNodeInfo;

import com.fast.savenode.UiAutomationShellWrapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class NodeUtil {
    private static final boolean LDEBUG = false;
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String TAG = df.format(new Date()) + " / NodeUtil: ";
    //测试变量 ⬇️
    private static List<AccessibilityNodeInfo> nodeResult = new ArrayList<>();

    private static void dfs(AccessibilityNodeInfo nodeInfo, List<PointF> result, StringBuilder builder) {
        if (LDEBUG) {
            System.out.println(TAG+" -------------dfs---------------");
        }

        int count = nodeInfo.getChildCount();
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
            if (child != null) {
                if (LDEBUG) {
                    String className = child.getClassName().toString();
                    String text = child.getText() == null ? "null" : child.getText().toString();
                    System.out.println(TAG + " child["+i+ "] name=" + nodeInfo + builder.toString() + className+
                            ", child text=" + builder.toString() + text);
                }

                if (judge(child)) {
                    if (LDEBUG) {
                        nodeResult.add(child);
                    }
                    result.add(getCenterPoint(child));
                }
                builder.append("-");
                dfs(child, result, builder);
                builder.deleteCharAt(0);
            }
        }
    }

    private static boolean judge(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo.getChildCount() > 0) {
            return false;
        }

        if (!isInRange(getCenterPoint(nodeInfo))) {
            return false;
        }

        String clickableFilter = FileUtils.getProperty(FileUtils.ClickableFilter);
        if ("on".equals(clickableFilter) && !isValidArea(nodeInfo)) {
            return false;
        }

        String areaFilter = FileUtils.getProperty(FileUtils.AreaFilter);
        if ("on".equals(areaFilter) && !isClickable(nodeInfo) && !isLongClickable(nodeInfo)) {
            return false;
        }

        if (LDEBUG) {
            System.out.println("ClickableFilter=" + clickableFilter + ", areaFilter=" + areaFilter);
        }
        return true;
    }


    private static List<PointF> getList() {
        UiAutomationShellWrapper automationWrapper = new UiAutomationShellWrapper();
        automationWrapper.connect();
        System.out.println(TAG + " automationWrapper connected...");

        try {
            UiAutomation uiAutomation = automationWrapper.getUiAutomation();
            uiAutomation.waitForIdle(100, 1000 * 10);
            AccessibilityNodeInfo info = uiAutomation.getRootInActiveWindow();
            if (info == null) {
                System.err.println("ERROR: null root node returned by UiTestAutomationBridge.");
                return null;
            }

            List<PointF> result = new ArrayList<>();
            StringBuilder builder = new StringBuilder("-");
            dfs(info, result, builder);

            if (LDEBUG) {
                System.out.println(TAG + " centerPoint="+ result.toString() + ", resultLength=" + result.size());
                System.out.println(TAG + " nodeResult="+ nodeResult.toString() + ", resultLength=" + nodeResult.size());
            }
            return result;
        } catch (TimeoutException re) {
            System.err.println("ERROR: could not get idle state.");
            return null;
        } finally {
            automationWrapper.disconnect();
        }
    }

    private static boolean isClickable(AccessibilityNodeInfo nodeInfo) {
        return nodeInfo.isClickable();
    }

    private static boolean isLongClickable(AccessibilityNodeInfo nodeInfo) {
        return nodeInfo.isLongClickable();
    }

    private static boolean isValidArea(AccessibilityNodeInfo nodeInfo) {
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        int height = rect.height();
        int width = rect.width();
        if (height > 0 && width > 0) {
            return true;
        }
        return false;
    }

    private static boolean isInRange(PointF pointF) {
        Display display = DisplayManagerGlobal.getInstance().getRealDisplay(Display.DEFAULT_DISPLAY);
        if ((pointF.x < 0) || (pointF.x > display.getWidth())) {
            return false;
        }
        if ((pointF.y < 0) || (pointF.y > display.getHeight())) {
            return false;
        }
        return true;
    }

    private static PointF getCenterPoint(AccessibilityNodeInfo nodeInfo) {
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        int X = rect.centerX();
        int Y = rect.centerY();
        return new PointF(X, Y);
    }

    public static PointF getRandomPoint(Random random, Display display) {
        List<PointF> pointFList = getList();
        if (pointFList == null) {
            return new PointF(random.nextInt(display.getWidth()), random.nextInt(display.getHeight()));
        }

        int count = pointFList.size();
        if (count <= 0) {
            return new PointF(random.nextInt(display.getWidth()), random.nextInt(display.getHeight()));
        }

        int index = random.nextInt(count);
        System.out.println(TAG + "randomPoint=" + pointFList.get(index));
        return pointFList.get(index);

    }
}
