package com.bj58.etx.boot.init;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bj58.etx.api.db.IEtxDao;
import com.bj58.etx.api.exception.EtxException;
import com.bj58.etx.api.serialize.IEtxSerializer;
import com.bj58.etx.boot.dao.MongoDao;
import com.bj58.etx.boot.dao.MysqlDao;
import com.bj58.etx.boot.helper.IdHelper;
import com.bj58.etx.boot.helper.MongoHelper;
import com.bj58.etx.boot.helper.MysqlHelper;
import com.bj58.etx.core.cache.EtxClassCache;
import com.bj58.etx.core.runtime.EtxRuntime;

public class EtxInit {

	private static volatile AtomicBoolean isInit = new AtomicBoolean(false);
	private static Log log = LogFactory.getLog(EtxInit.class);

	/**
	 * 初始化Etx运行环境
	 */
	public static void init(String path) {
		if (!isInit.get()) {
			log.info("------初始化Etx环境,path=" + path);
			try {
				Properties p = new Properties();
				File file = new File(path);
				p.load(new FileInputStream(file));
				
				String daoClass  = p.getProperty("etx.daoClass");
				String serializeClass  = p.getProperty("etx.serializeClass");
				String mysqlConfiPath  = p.getProperty("etx.mysqlConfiPath");
				String mongoConfigPath  = p.getProperty("etx.mongoConfigPath");
				String serverIdConfigPath  = p.getProperty("etx.serverIdConfigPath");
				String dbHeatBeatInterval  = p.getProperty("etx.dbHeatBeatInterval");
				String dbMaxFailCount  = p.getProperty("etx.dbMaxFailCount");
				String processThreadCount  = p.getProperty("etx.processThreadCount");
				String taskThreadCount  = p.getProperty("etx.taskThreadCount");
				String taskLoopTxInterval  = p.getProperty("etx.taskLoopTxInterval");
				String countForCancelOnce  = p.getProperty("etx.countForCancelOnce");
				String countForDoAsyncOnce  = p.getProperty("etx.countForDoAsyncOnce");
				
				if(StringUtils.isNotBlank(daoClass)){
					IEtxDao dao= EtxClassCache.getInstance(daoClass);
					EtxRuntime.setDao(dao);
				}
				
				if(StringUtils.isNotBlank(serializeClass)){
					IEtxSerializer serializer= EtxClassCache.getInstance(serializeClass);
					EtxRuntime.setSerializer(serializer);
				}
				
				if(StringUtils.isNotBlank(dbHeatBeatInterval)){
					EtxRuntime.setDbHeatBeatInterval(Integer.valueOf(dbHeatBeatInterval));
				}
				if(StringUtils.isNotBlank(dbMaxFailCount)){
					EtxRuntime.setDbMaxFailCount(Integer.valueOf(dbMaxFailCount));
				}
				if(StringUtils.isNotBlank(processThreadCount)){
					EtxRuntime.setProcessThreadCount(Integer.valueOf(processThreadCount));
				}
				if(StringUtils.isNotBlank(taskThreadCount)){
					EtxRuntime.setTaskThreadCount(Integer.valueOf(taskThreadCount));
				}
				if(StringUtils.isNotBlank(taskLoopTxInterval)){
					EtxRuntime.setTaskLoopTxInterval(Integer.valueOf(taskLoopTxInterval));
				}
				if(StringUtils.isNotBlank(countForCancelOnce)){
					EtxRuntime.setCountForCancelOnce(Integer.valueOf(countForCancelOnce));
				}
				if(StringUtils.isNotBlank(countForDoAsyncOnce)){
					EtxRuntime.setCountForDoAsyncOnce(Integer.valueOf(countForDoAsyncOnce));
				}
				
				
				if(StringUtils.isNotBlank(mysqlConfiPath) && MysqlDao.class.getName().equals(daoClass)){
					MysqlHelper.init(getPath(file, mysqlConfiPath));
				}
				
				if(StringUtils.isNotBlank(mongoConfigPath) && MongoDao.class.getName().equals(daoClass)){
					MongoHelper.init(getPath(file, mongoConfigPath));
				}
				
				if(StringUtils.isNotBlank(serverIdConfigPath)){
					IdHelper.init(getPath(file, serverIdConfigPath));
				}
				
				
			} catch (Exception e) {
				log.error("EtxInit error", e);
				throw new EtxException(e);
			}
		}
	}

	// 获取etx 配置文件里路径的绝对路径（配置文件里可能是相对路径）
	private static String getPath(File etxConfigFile, String subPath) {

		// 如果是绝对路径，则直接返回
		if (subPath != null && (subPath.contains("/") || subPath.contains(File.separator))) {
			return subPath;
		}

		if (etxConfigFile.getParent() != null) {
			return etxConfigFile.getParent() + File.separator + subPath;
		}

		return subPath;

	}
}