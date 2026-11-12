package com.workspace.app.service;

import com.workspace.app.model.BlockType;
import com.workspace.app.model.PageBlock;
import com.workspace.app.repository.BlockRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlockService {
    private final BlockRepository blockRepository;

    public BlockService(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    public List<PageBlock> getBlocksForPage(String pageId) {
        List<PageBlock> list = blockRepository.findByPageId(pageId);
        if (list.isEmpty()) {
            PageBlock defaultBlock = new PageBlock("b_" + UUID.randomUUID().toString().substring(0, 8), pageId, BlockType.PARAGRAPH, "", 0);
            blockRepository.save(defaultBlock);
            list = new ArrayList<>();
            list.add(defaultBlock);
        }
        return list;
    }

    public void saveBlock(PageBlock block) {
        blockRepository.save(block);
    }

    public void saveAll(String pageId, List<PageBlock> blocks) {
        blockRepository.saveAllForPage(pageId, blocks);
    }

    public PageBlock createBlockAfter(String pageId, BlockType type, int position) {
        String id = "b_" + UUID.randomUUID().toString().substring(0, 8);
        return new PageBlock(id, pageId, type, "", position);
    }

    public void deleteBlock(String blockId) {
        blockRepository.delete(blockId);
    }
}
