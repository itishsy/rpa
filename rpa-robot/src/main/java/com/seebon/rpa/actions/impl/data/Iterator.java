package com.seebon.rpa.actions.impl.data;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.ObjectTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.IteratorType;
import com.seebon.rpa.context.runtime.RuntimeSkipTo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
@RobotAction(name = "遍历器", comment = "遍历List或Array。索引变量为index，对象变量为item。", targetType = ObjectTarget.class, order = 1)
public class Iterator extends AbstractAction {

    public static final String ITERATOR_INDEX = "index";
    public static final String ITERATOR_ITEM = "item";
    public static final String ITERATOR_SIZE = "iterator_size_";
    public static final String ITERATOR_CODE = "iterator_";

    @ActionArgs(value = "遍历类型", required = true, dict = IteratorType.class)
    private String iteratorType;

    @ActionArgs(value = "对象变量名称")
    private String itemKey;

    @ActionArgs(value = "索引变量名称")
    private String indexKey;

    @Override
    public void run() {
        String iteratorCode = ITERATOR_CODE + stepCode;
        String iteratorSize = ITERATOR_SIZE + stepCode;
        Object data = getTarget();
        int size = 0;
        Integer index = 0;
        if (!ctx.contains(iteratorCode)) {
            ctx.setVariable(iteratorCode, iteratorCode);
            ctx.setVariable(StringUtils.isBlank(indexKey) ? ITERATOR_INDEX : indexKey, index);
            switch (IteratorType.valueOf(iteratorType)) {
                case list: {
                    size = ((List) data).size();
                    break;
                }
                case array: {
                    size = ((Object[]) data).length;
                    break;
                }
                case integer: {
                    size = Integer.parseInt(data.toString());
                    break;
                }
                case string: {
                    size = data.toString().split(",").length;
                    break;
                }
                default:
                    break;
            }
            ctx.setVariable(iteratorSize, size);
        } else {
            index = ctx.getVariable(StringUtils.isBlank(indexKey) ? ITERATOR_INDEX : indexKey);
            index++;
            size = ctx.getVariable(iteratorSize);
        }
        Object item = null;
        if (size == 0) {
            log.warn("警告:遍历对象的长度为0");
            ctx.remove(StringUtils.isBlank(indexKey) ? ITERATOR_INDEX : indexKey);
            ctx.remove(StringUtils.isBlank(itemKey) ? ITERATOR_ITEM : itemKey);
            ctx.remove(iteratorSize);
            ctx.remove(iteratorCode);
            throw new RuntimeSkipTo(skipTo);
        }
        log.info("Iterator size:" + size + ",index:" + index);
        if (index < size) {
            switch (IteratorType.valueOf(iteratorType)) {
                case list: {
                    item = ((List) data).get(index);
                    break;
                }
                case array: {
                    item = ((Object[]) data)[index];
                    break;
                }
                case integer: {
                    item = index;
                    break;
                }
                case string: {
                    item = data.toString().split(",")[index];
                    break;
                }
                default:
                    break;
            }
            ctx.setVariable(StringUtils.isBlank(indexKey) ? ITERATOR_INDEX : indexKey, index);
            ctx.setVariable(StringUtils.isBlank(itemKey) ? ITERATOR_ITEM : itemKey, item);
            throw new RuntimeSkipTo(RobotConstant.NEXT_STEP);
        } else {
            ctx.remove(StringUtils.isBlank(indexKey) ? ITERATOR_INDEX : indexKey);
            ctx.remove(StringUtils.isBlank(itemKey) ? ITERATOR_ITEM : itemKey);
            ctx.remove(iteratorSize);
            ctx.remove(iteratorCode);
            throw new RuntimeSkipTo(skipTo);
        }
    }
}
