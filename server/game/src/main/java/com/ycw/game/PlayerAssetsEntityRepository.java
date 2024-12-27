package com.ycw.game;

import com.ycw.core.internal.db.repository.CachedRepository;

public class PlayerAssetsEntityRepository extends CachedRepository<PlayerAssetsEntity> {

    PlayerAssetsEntity getOrMakeAssetsEntity(long roleId) {
	return makeSure(roleId);
    }
}
