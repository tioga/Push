package org.tiogasolutions.push.pub.internal;

import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.pub.PubItem;
import org.tiogasolutions.pub.PubStatus;

import java.util.LinkedList;
import java.util.List;

public class PushExceptionInfo extends PubItem {

    private final List<String> causes = new LinkedList<>();

    public PushExceptionInfo(int status, Throwable ex) {
        super(new PubStatus(status, ExceptionUtils.getMessage(ex)));

        List<? extends Throwable> allCauses = ExceptionUtils.getRootCauses(ex);

        for (Throwable cause : allCauses) {
            String msg = ExceptionUtils.getMessage(cause);
            causes.add(msg);
        }

        causes.remove(0); // Remove the original exception.
    }

    public List<String> getCauses() {
        return causes;
    }
}
