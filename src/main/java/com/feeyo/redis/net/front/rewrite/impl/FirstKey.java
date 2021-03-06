package com.feeyo.redis.net.front.rewrite.impl;

import com.feeyo.net.codec.redis.RedisRequest;
import com.feeyo.redis.config.UserCfg;
import com.feeyo.redis.net.front.rewrite.KeyIllegalException;
import com.feeyo.redis.net.front.rewrite.KeyRewriteStrategy;

/**
 * 变换第一个Key
 * @author zhuam
 *
 */
public class FirstKey extends KeyRewriteStrategy {

	@Override
	public void rewriteKey(RedisRequest request, UserCfg userCfg) throws KeyIllegalException {
        int numArgs = request.getNumArgs();
        if (numArgs < 2) {
            return;
        }
		byte[][] args = request.getArgs();
		
		checkKeyIllegalCharacter(userCfg.getKeyExpr(), args[1]);
		//修改前缀
		args[1] = concat(userCfg.getPrefix(), args[1]);

	}

	@Override
	public byte[] getKey(RedisRequest request) {
		if ( request.getNumArgs() < 2) {
			return null;
		}
		byte[][] args = request.getArgs();
		return args[1];
	}

}
