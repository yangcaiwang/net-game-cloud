package com.game.server;

import com.common.module.internal.db.repository.CachedRepository;

public class PlayerAssetsEntityRepository extends CachedRepository<PlayerAssetsEntity> {

    PlayerAssetsEntity getOrMakeAssetsEntity(long roleId) {
	return makeSure(roleId);
    }
}
