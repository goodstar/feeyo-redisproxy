package com.feeyo.redis.net.front.rewrite.impl;

import com.feeyo.net.codec.redis.RedisRequest;
import com.feeyo.redis.config.UserCfg;
import com.feeyo.redis.net.front.rewrite.KeyIllegalException;
import com.feeyo.redis.net.front.rewrite.KeyRewriteStrategy;

/**
 * 针对Set指令
 * 
 * @author dsliu
 *  SET key value [EX seconds] [PX milliseconds] [NX|XX]
 */
public class SetKey extends KeyRewriteStrategy {
	
	// PX， 将键的过期时间设置为 milliseconds
	private static final byte[] PX_BYTES = "PX".getBytes();

	@Override
	public void rewriteKey(RedisRequest request, UserCfg userCfg) throws KeyIllegalException {
        int numArgs = request.getNumArgs();
        if (numArgs < 2) {
            return;
        }
        
		byte[][] args = request.getArgs();
		
		// 校验 key 规则
		checkKeyIllegalCharacter(userCfg.getKeyExpr(), args[1]);
		
        //修改前缀
        args[1] = concat(userCfg.getPrefix(), args[1]);

        //针对没有过期时间的添加 默认过期时间
        if (numArgs == 3) {
        	// SET key value PX milliseconds
            byte[][] newArgs = new byte[][]{args[0], args[1], args[2], PX_BYTES, userCfg.getKeyExpireTime()};
            request.setArgs(newArgs);
            
        } else if (numArgs == 4) {
        	// SET key value PX milliseconds NX or XX
            byte[][] newArgs = new byte[][]{args[0], args[1], args[2], PX_BYTES, userCfg.getKeyExpireTime(), args[3]};
            request.setArgs(newArgs);
        }

	}

	@Override
	public byte[] getKey(RedisRequest request) {
		if ( request.getNumArgs() < 2) 
			return null;
		//
		byte[][] args = request.getArgs();
		return args[1];
	}

}
