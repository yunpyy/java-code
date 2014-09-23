package com.finegold.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RenderedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import com.finegold.dao.ArchiveUserDAO;
import com.finegold.dao.BaseDAO;
import com.finegold.dao.CompanyUserDAO;
import com.finegold.dao.LySignatureDAO;
import com.finegold.dao.LySignpackagenumDAO;
import com.finegold.dao.TbSystemmlogDAO;
import com.finegold.entity.ArchivesFileXml;
import com.finegold.entity.CompanyUser;
import com.finegold.entity.LySignature;
import com.finegold.entity.LySignpackagenum;
import com.finegold.entity.LyUser;
import com.finegold.entity.TbSystemmlog;
import com.finegold.entity.UserInfo;

import com.finegold.exception.DbException;
import com.finegold.util.DateUtil;
import com.finegold.util.Page;
import com.finegold.util.Tool;
import com.finegold.util.XmlDocUtil;
import com.sun.media.imageio.plugins.tiff.TIFFTag;
import com.sun.media.jai.codec.BMPEncodeParam;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.TIFFDirectory;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codec.TIFFField;

/**
 * 
 * @author hzjwin7
 *
 */
public class LySignatureHelper extends BaseHelper {
	@SuppressWarnings("unused")
	private static  Logger log=Logger.getLogger(LySignatureHelper.class);
	public static LySignatureDAO getDao() {
		LySignatureDAO dao = (LySignatureDAO) AppContext.getBean("LySignatureDAO");
	    return dao;
	  }
	public static LySignature load(Long pvid) throws DbException {
		return (LySignature) load(LySignature.class, pvid);
	}
	public static LySignpackagenumDAO getSignDao() {
		LySignpackagenumDAO dao = (LySignpackagenumDAO) AppContext.getBean("LySignpackagenumDAO");
	    return dao;
	  }
	
	/**
	 * 閼惧嘲褰噒if閺傚洣娆?
	 */
	public static void FetchFromFTP(String fileurl , String ftptif , String mFileName ){
		try {
		  FTPClient ftp = new FTPClient();
		  ftp.connect("192.168.3.72", 21);  
		  ftp.login("pddajd", "pddajd");  
		  int reply = ftp.getReplyCode();
		  if (FTPReply.isPositiveCompletion(reply)) {
			String ftpworkpath = fileurl.substring(0,fileurl.lastIndexOf("/") + 1) ;
			ftp.changeWorkingDirectory( ftpworkpath );   
			//System.out.println("ftpworkpath="+ftpworkpath);
			reply = ftp.getReplyCode(); 
			if(FTPReply.CODE_550 == reply){}
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			String localpath = ftptif ; 
			//System.out.println("localpath="+localpath);
			//System.out.println("mFileName="+mFileName);
			new File(localpath).mkdirs();
			String localfile = localpath +  mFileName ;//娑撳娴囬崥搴ｆ畱閺堫剙婀撮弬鍥︽閸氾拷
			//System.out.println("localfile="+localfile);
			File localFile = new File(localfile); 
			try{
				OutputStream is = new FileOutputStream(localFile);   
				ftp.retrieveFile(mFileName, is);
				is.close(); 
				
				localfile = resetTIF(localfile);
				//Thread.sleep(12000);
			}catch(Exception ex){
				  ex.printStackTrace();
			}finally{
				 
			}
		  }else{
			ftp.disconnect();
		  }
		 } catch (Exception e) {   
	        e.printStackTrace();   
	    }
	}
	/**
	 * 鏉烆剚宕瞭if閺傚洣娆?
	 */
	public static void splitTIFF(String ftptif , String mFileName , String flag , String splitpath , java.util.List<String> mergintifs){
		boolean isdebug = true;
		int width  = 1700 ;
		int height = 2300 ;
		try { 
			SeekableStream seekableStream = new FileSeekableStream(new File(ftptif + mFileName));  
	        TIFFDecodeParam param = new TIFFDecodeParam();  
	        ImageDecoder imageDecoder = ImageCodec.createImageDecoder("tiff", seekableStream, param);  
	        int numPages = imageDecoder.getNumPages();  
	        RenderedImage outImage = null;
	        ImageEncoder imageEncoder = null;
	        OutputStream outStr;
	        TIFFEncodeParam encodedParameters = new TIFFEncodeParam(); 
			encodedParameters.setCompression(TIFFEncodeParam.COMPRESSION_GROUP4);
	        for (int i = 0; i < numPages; i++) {
	            outImage = imageDecoder.decodeAsRenderedImage(i);
				String disfile = splitpath +  flag + "_" + i + ".tiff";
	            outStr = new FileOutputStream(disfile);
				try{
		            //鏉烆剚宕查幋鎭噈p閺嶇厧绱?
	            	long s = System.currentTimeMillis();
		            ByteArrayOutputStream out = new ByteArrayOutputStream();
					BMPEncodeParam bmpparam = new BMPEncodeParam();					 
		            ImageEncoder enc = ImageCodec.createImageEncoder("BMP", out , new BMPEncodeParam());
			        enc.encode(outImage); 
					if(isdebug){
						System.out.println("1 = " + (System.currentTimeMillis() - s) );
						s = System.currentTimeMillis();
					}
			        //閸ュ搫鐣綽mp婢堆冪毈
			        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			        BufferedImage img = ImageIO.read(in); 
		            int imageType = img.getType();
		            int w = img.getWidth();  
		            int h = img.getHeight(); 
		            float sfbl = 1l; 
		            if(w > width || h > height){
			            BigDecimal   b  =   new  BigDecimal(1 - (float)(w-width)/w);  
			            float   sfblw   =  b.setScale(2,  BigDecimal.ROUND_HALF_UP).floatValue();  
			            b  =   new  BigDecimal(1 - (float)(h-height) / h);  
			            float   sfblh   =  b.setScale(2,  BigDecimal.ROUND_HALF_UP).floatValue();  
			            sfbl = sfblw < sfblh ? sfblw : sfblh ;
		            } 
		            BufferedImage imgOut = new BufferedImage(width, height, imageType ); 
					//
					/*
					int[] rgbArray1 = new int[ height * width + width ] ;
					for(int j = 0 ; j < rgbArray1.length ; j++){ 
						rgbArray1[j] = -1 ;
					}
					imgOut.setRGB(0, 0, width, height , rgbArray1, 0, width) ;
					*/
					//
		            Graphics2D dg = (Graphics2D) imgOut.createGraphics(); 
		            dg.setBackground(Color.WHITE);
		            dg.fillRect(0,0,width ,height);
		            AffineTransform at = new AffineTransform();
		            at.scale(sfbl, sfbl); 
		            BufferedImageOp bi = new AffineTransformOp(at, null); 
		            if(sfbl < 1 ){
		            	int rw = (int)( w*sfbl) ;
		            	int rh = (int)(h*sfbl );
		            	dg.drawImage(img, bi, (width-rw) / 2 , (height-rh) / 2 );
		            }else
		            	dg.drawImage(img, bi, (width-w) / 2 , (height-h) / 2 ); 
		            dg.dispose();
					//
					int[] rgbArray2 = new int[ height * width + width ]; 
					imgOut.getRGB(0, 0, width, height, rgbArray2, 0, width);
					for(int j = 0 ; j < rgbArray2.length ; j++){ 
						if(rgbArray2[j] == -1 || rgbArray2[j] == 0 )
							rgbArray2[j] = -16777216;
						else 
							rgbArray2[j] = -1; 
					} 
					imgOut.setRGB(0, 0, width, height , rgbArray2, 0, width);
					//
		            out.close();
		            in.close(); 
					if(isdebug){
						System.out.println("2 = " + (System.currentTimeMillis() - s) );
						s=System.currentTimeMillis();
					}
		            //鏉烆剚宕查幋鎭f閺嶇厧绱?
		            OutputStream os = new FileOutputStream(disfile); 
		            TIFFEncodeParam tifparam = new TIFFEncodeParam();
		            tifparam.setCompression(TIFFEncodeParam.COMPRESSION_GROUP4); 
		            TIFFField[] extras = new TIFFField[2]; 
            			extras[0] = new TIFFField(282, TIFFTag.TIFF_RATIONAL, 1, (Object) new long[][]{{(long) 200, 1}, {0, 0}});
            			extras[1] = new TIFFField(283, TIFFTag.TIFF_RATIONAL, 1, (Object) new long[][]{{(long) 200, 1}, {0, 0}});
	        		tifparam.setExtraFields(extras);
			        ImageEncoder enc1 = ImageCodec.createImageEncoder("TIFF", os, tifparam);  
			        enc1.encode(imgOut); 
			        os.close();
					if(isdebug){
						System.out.println("3 = " + (System.currentTimeMillis() - s) );
					}
	            }catch (Exception e) { 
	            	BufferedOutputStream bOutStr = new BufferedOutputStream(new FileOutputStream(disfile));
	            	ImageEncoder imageEncoder1 = ImageCodec.createImageEncoder("tiff", bOutStr, encodedParameters);
		            imageEncoder1.encode(outImage);
		            bOutStr.flush();
		            bOutStr.close();
	    	    }
				//
				mergintifs.add(disfile);
	        }
		} catch (Exception e) {   
	        e.printStackTrace();   
	    } 
	}
	public static void mergineTIFF(java.util.List<String> tifs , String merginefile){
		try
		{
			File[] files = new File[tifs.size()]; 
			for(int i = 0 ; i < tifs.size() ; i++){
				files[i] = new File((String)tifs.get(i));
			} 
			ArrayList pages = new ArrayList(files.length - 1);
			RenderedOp firstPage =	JAI.create("fileload", files[0].getCanonicalPath());
			for (int i = 1; i < files.length; i++)
			{
				RenderedOp page = JAI.create("fileload", files[i].getCanonicalPath());
				pages.add(page);
			}
			TIFFEncodeParam param = new TIFFEncodeParam();
			//param.setCompression(TIFFEncodeParam.COMPRESSION_PACKBITS);
			param.setCompression(TIFFEncodeParam.COMPRESSION_GROUP4);
			TIFFField[] extras = new TIFFField[2]; 
            		extras[0] = new TIFFField(282, TIFFTag.TIFF_RATIONAL, 1, (Object) new long[][]{{(long) 200, 1}, {0, 0}});
            		extras[1] = new TIFFField(283, TIFFTag.TIFF_RATIONAL, 1, (Object) new long[][]{{(long) 200, 1}, {0, 0}});
	        	param.setExtraFields(extras);
			//閸氬牆鑻熼崥搴ｆ晸閹存劗娈戦崶鍓у
			OutputStream out = new FileOutputStream(merginefile);
			ImageEncoder encoder =
			ImageCodec.createImageEncoder("TIFF", out, param);
			param.setExtraImages(pages.iterator());
			encoder.encode(firstPage);
			firstPage.dispose();
			for (int i = 0; i < pages.size(); i++)
			{
				((RenderedOp)pages.get(i)).dispose();
			}
			out.close();
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}
public static Map<String,String> getSignPackageUrl(String mWebPath,Long adminID,String fileId,String aid){
	java.util.List<String> mergintifs = new ArrayList<String>();
	Map<String,String> urlAndCzh=new HashMap<String, String>();
	String worktmppath = mWebPath + "\\worktmp\\"+adminID;
	String ftptifpath = worktmppath + "\\ftptif\\" ;
	String splitpath  = worktmppath + "\\split\\" ;
	String merginfile = worktmppath + "\\ps.tif";
	String R_merginfile = "/worktmp/"+adminID + "/ps.tif"; //閸氬牆鑻焧if閺傚洣娆㈤惃鍕祲鐎佃鏋冩禒璺烘倳
	String czh="";//娴溠嗙槈閸欙拷
	DeleteFolder(ftptifpath);
	DeleteFolder(splitpath); 
	new File(ftptifpath).mkdirs();
	new File(splitpath ).mkdirs();
	String path1 = "";
	String[] fileIds = fileId.split(",");
	
	//避免档号6.D4.7.1999-015445打包签章时出错
	if(fileIds.length > 6){
		if(StringUtils.contains(fileId, "1354226") || StringUtils.contains(fileId, "1354227") ||
			StringUtils.contains(fileId, "1354228") || StringUtils.contains(fileId, "1354229") ||
			StringUtils.contains(fileId, "1354230") || StringUtils.contains(fileId, "1354231") ||
			StringUtils.contains(fileId, "1354232") || StringUtils.contains(fileId, "1354233")){
				fileIds = new String[6];
				fileIds[0] = "1354226";
				fileIds[1] = "1354227";
				fileIds[2] = "1354228";
				fileIds[3] = "1354229";
				fileIds[4] = "1354230";
				fileIds[5] = "1354231";
			}
	}
	
	//创建打包签章展示进度的实例
	LySignpackagenum signnum = new LySignpackagenum();
	
	for(int i=0;i<fileIds.length;i++){
		try {
			//把当前读取第i个签章存入nums,共多少签章记录存入total并保存一直修改此条数据
			signnum.setId(1L);
			signnum.setNums(Long.valueOf(i));
			signnum.setTotal(Long.valueOf(fileIds.length));
			getSignDao().saveOrUpdate(signnum);
			
			ArchivesFileXml afx=(ArchivesFileXml) ArchivesFileXmlHelper.load(ArchivesFileXml.class, Long.parseLong(fileIds[i]));
			afx.setStringContent(ArchivesXmlHelper.fetchContent(Long.parseLong(aid)));
			if (afx==null) continue;
			String cdPath=afx.getCdpath();
			if( cdPath==null||"".equals(cdPath.trim())) {
				continue;
			}else{
				char wrong=92;
				char right=47;
				cdPath = cdPath.replace(wrong , right);
			}
			String aCode=afx.getAcode();
			AcodeParse ap = new AcodeParse(aCode);
			path1 = ap.getTiffPath();
			String filenameAll = path1+"/"+cdPath ; 
			String tiffilename = cdPath.substring(cdPath.lastIndexOf("/") + 1) ;
			if (aCode.indexOf(".D4.7.")!=-1) {
				String content=afx.getStringContent();
				if (content!=null) {
					Document doc=XmlDocUtil.Xml2Document(content);
					XPath path=XPath.newInstance("//FC14");
					Element e=(Element) path.selectSingleNode(doc);
					if (e!=null) {
						czh=e.getValue();
					}
				}
			}
			FetchFromFTP(filenameAll,ftptifpath,  tiffilename ); 
			splitTIFF(ftptifpath,tiffilename ,"" + i , splitpath , mergintifs);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	try {
		getSignDao().delete(signnum);
	} catch (DbException e) {
		e.printStackTrace();
	}
	mergineTIFF(mergintifs,merginfile);
	DeleteFolder(ftptifpath);
	DeleteFolder(splitpath); 
	urlAndCzh.put("url", R_merginfile);
	urlAndCzh.put("czh", czh);
	return urlAndCzh;
}
public static Map<String,String> getsignSingleUrl(String mWebPath,Long adminID,String fileId,String aid){
	Map<String,String> urlAndCzh=new HashMap<String, String>();
	String worktmppath = mWebPath + "\\worktmp\\"+adminID;
	String ftptifpath = worktmppath + "\\ftptif\\single\\" ;
	String savepath="/worktmp/"+adminID+"/ftptif/single/";
	String czh="";//娴溠嗙槈閸欙拷
	
//	String R_merginfile = "/worktmp/"+adminID + "/ps.tif";
//	String splitpath  = worktmppath + "\\split\\" ;
//	String merginfile = worktmppath + "\\ps.tif";
//	java.util.List<String> mergintifs = new ArrayList<String>();
	
	DeleteFolder(ftptifpath);
	new File(ftptifpath).mkdirs();
	String path1 = "";
	try {
		ArchivesFileXml afx=(ArchivesFileXml) ArchivesFileXmlHelper.load(ArchivesFileXml.class, Long.parseLong(fileId));
		afx.setStringContent(ArchivesXmlHelper.fetchContent(Long.parseLong(aid)));
		String cdPath=afx.getCdpath();
		if( cdPath!=null&&!"".equals(cdPath.trim())){
			char wrong=92;
			char right=47;
			cdPath = cdPath.replace(wrong , right);
		}
		String aCode=afx.getAcode();
		AcodeParse ap = new AcodeParse(aCode);
		path1 = ap.getTiffPath();
		String filenameAll = path1+"/"+cdPath ; 
		String tiffilename = cdPath.substring(cdPath.lastIndexOf("/") + 1) ;
		if (aCode.indexOf(".D4.7.")!=-1) {
			String content=afx.getStringContent();
			if (content!=null) {
				Document doc=XmlDocUtil.Xml2Document(content);
				XPath path=XPath.newInstance("//FC14");
				Element e=(Element) path.selectSingleNode(doc);
				if (e!=null) {
					czh=e.getValue();
				}
			}
		}
		savepath+="/"+ "new" + tiffilename;
		FetchFromFTP(filenameAll,ftptifpath,  tiffilename );
		
		/*
		//签章有时被缩小,对有问题的档号的tiff文件按照打包签章的流程处理
		if(StringUtils.equals("138621", aid) ||
				StringUtils.equals("1804215", aid) ||
				StringUtils.equals("137733", aid)){
			if(StringUtils.equals("00000017.tif", tiffilename) || 
					StringUtils.equals("00000005.tif", tiffilename) ||
					StringUtils.equals("00000001.tif", tiffilename)){
				
			}else {
				splitTIFF(ftptifpath, tiffilename, "", splitpath, mergintifs);
				mergineTIFF(mergintifs,merginfile);
//				DeleteFolder(ftptifpath);
//				DeleteFolder(splitpath); 
			}
		}
		*/
		/*
		//预先在项目目录下worktmp放入单个签章有问题档号的tiff文件，加快打开速度
		if(StringUtils.equals("138621", aid)){
			if(StringUtils.equals("00000017.tif", tiffilename)){
				R_merginfile = "/worktmp/1/017.tif";
			}
		}else if(StringUtils.equals("1804215", aid)){
			if(StringUtils.equals("00000005.tif", tiffilename)){
				R_merginfile = "/worktmp/1/020.tif";
			}
		}else if(StringUtils.equals("137733", aid)){
			if(StringUtils.equals("00000001.tif", tiffilename)){
				R_merginfile = "/worktmp/1/tongxiangyu.tif";
			}
		}
		*/
		
	} catch (NumberFormatException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (DbException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	urlAndCzh.put("url", savepath);
	/*
	//签章有时被缩小,对此档号的tiff文件按照打包签章的流程处理
	if(StringUtils.equals("138621", aid) ||
			StringUtils.equals("1804215", aid) ||
			StringUtils.equals("137733", aid)){
		urlAndCzh.put("url", R_merginfile);
	}
	*/
	urlAndCzh.put("czh", czh);
	return urlAndCzh;
}
public  static boolean  DeleteFolder(String sPath) {   
    boolean flag = false;   
    File file = new File(sPath);   
    // 閸掋倖鏌囬惄顔肩秿閹存牗鏋冩禒鑸垫Ц閸氾箑鐡ㄩ崷锟? 
    if (!file.exists()) {  // 娑撳秴鐡ㄩ崷銊ㄧ箲閸ワ拷false   
        return flag;   
    } else {   
        // 閸掋倖鏌囬弰顖氭儊娑撶儤鏋冩禒锟? 
        if (file.isFile()) {  // 娑撶儤鏋冩禒鑸垫鐠嬪啰鏁ら崚鐘绘珟閺傚洣娆㈤弬瑙勭《   
            return deleteFile(sPath);   
        } else {  // 娑撹櫣娲拌ぐ鏇熸鐠嬪啰鏁ら崚鐘绘珟閻╊喖缍嶉弬瑙勭《   
            return deleteDirectory(sPath);   
        }   
    }   
}

/**  
 * 閸掔娀娅庨崡鏇氶嚋閺傚洣娆? 
 * @param   sPath    鐞氼偄鍨归梽銈嗘瀮娴犲墎娈戦弬鍥︽閸氾拷 
 * @return 閸楁洑閲滈弬鍥︽閸掔娀娅庨幋鎰鏉╂柨娲杢rue閿涘苯鎯侀崚娆掔箲閸ョ?alse  
 */  
public static boolean deleteFile(String sPath) {   
	boolean flag = false;   
	File file = new File(sPath);   
    // 鐠侯垰绶炴稉鐑樻瀮娴犳湹绗栨稉宥勮礋缁屽搫鍨潻娑滎攽閸掔娀娅?  
    if (file.isFile() && file.exists()) {   
        flag = file.delete();   
        flag = true;   
    }   
    return flag;   
}

/**  
 * 閸掔娀娅庨惄顔肩秿閿涘牊鏋冩禒璺恒仚閿涘浜掗崣濠勬窗瑜版洑绗呴惃鍕瀮娴狅拷 
 * @param   sPath 鐞氼偄鍨归梽銈囨窗瑜版洜娈戦弬鍥︽鐠侯垰绶? 
 * @return  閻╊喖缍嶉崚鐘绘珟閹存劕濮涙潻鏂挎礀true閿涘苯鎯侀崚娆掔箲閸ョ?alse  
 */  
public static boolean deleteDirectory(String sPath) {   
    //婵″倹鐏塻Path娑撳秳浜掗弬鍥︽閸掑棝娈х粭锔剧波鐏忔拝绱濋懛顏勫З濞ｈ濮為弬鍥︽閸掑棝娈х粭锟? 
    if (!sPath.endsWith(File.separator)) {   
        sPath = sPath + File.separator;   
    }   
    File dirFile = new File(sPath);   
    //婵″倹鐏塪ir鐎电懓绨查惃鍕瀮娴犳湹绗夌?妯烘躬閿涘本鍨ㄩ懓鍛瑝閺勵垯绔存稉顏嗘窗瑜版洩绱濋崚娆擄拷閸戯拷  
    if (!dirFile.exists() || !dirFile.isDirectory()) {   
        return false;   
    }   
    boolean flag = true;   
    //閸掔娀娅庨弬鍥︽婢堕?绗呴惃鍕閺堝鏋冩禒锟介崠鍛鐎涙劗娲拌ぐ锟?  
    File[] files = dirFile.listFiles();   
    for (int i = 0; i < files.length; i++) {   
        //閸掔娀娅庣?鎰瀮娴狅拷  
        if (files[i].isFile()) {   
            flag = deleteFile(files[i].getAbsolutePath());   
            if (!flag) break;   
        } //閸掔娀娅庣?鎰窗瑜帮拷  
        else {   
            flag = deleteDirectory(files[i].getAbsolutePath());   
            if (!flag) break;   
        }   
    }   
    if (!flag) return false;   
    //閸掔娀娅庤ぐ鎾冲閻╊喖缍?  
    if (dirFile.delete()) {   
        return true;   
    } else {   
        return false;   
    }   
}
/**
 * 绛剧珷鏌ヨ
 */
public static Page search(Map<String, String> paras, int curPage, int ps)throws DbException
{
	Page signs = null;
	List<Object> obj = new Vector<Object>();
	StringBuffer sb = new StringBuffer("from LySignature as t");
	try
	{
		if(paras!=null&&paras.size()!=0)
		{
			sb.append(" where 1=1");
			if(!Tool.isEmpty(paras.get("xlh")))//搴忓垪鍙?
			{
				obj.add("%"+paras.get("xlh").trim()+"%");
				sb.append(" and t.qzxlh like ?");
			}
			 if(!Tool.isEmpty(paras.get("dh")))//妗ｅ彿
			{
				obj.add("%"+paras.get("dh").trim()+"%");
				sb.append(" and t.acode like ?");
			}
			 if(!Tool.isEmpty(paras.get("qzuser")))//绛剧珷浜?
			{
				obj.add("%"+paras.get("qzuser").trim()+"%");
				sb.append(" and t.userinfo.name like ?");
			}
			 if(!Tool.isEmpty(paras.get("begin")))
			{
				obj.add(paras.get("begin"));
				sb.append(" and to_char(opttime,'yyyy-mm-dd')>=to_char(?)");
			}
			 if(!Tool.isEmpty(paras.get("end")))
			{
				obj.add(paras.get("end"));
				sb.append(" and to_char(opttime,'yyyy-mm-dd')<=to_char(?)");
			}
		}
		sb.append("order by id desc");
		LySignatureDAO dao=getDao();
		signs=dao.pageByHql(sb.toString(), obj.toArray(), curPage, ps);
	}
	 catch (Exception e) {
	      e.printStackTrace();
	      throw new DbException(e);
	    }
	return signs;
}

	/**
	 * 取当前打包签章正在读取的数值
	 * @return
	 */
	public static int getSignNums(){
		try {
			List<LySignpackagenum> numsList = getSignDao().findAll();
			if(numsList != null && numsList.size() != 0){
				return Integer.valueOf(String.valueOf(numsList.get(0).getNums()));
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String resetTIF(String tif){
		try{
	    	String tif1 = tif ;
	    	String localpath = tif1.substring(0,tif1.lastIndexOf(File.separator)) ; 
	    	String splitpath  = localpath + "\\split\\" ;
	    	String merginfile = localpath +File.separator+"new" + tif1.substring(tif1.lastIndexOf(File.separator) + 1 ) ;
	    	boolean includecolortif = false ; //是否包含彩色tif 
			DeleteFolder(splitpath);
			new File(splitpath ).mkdirs();
			//
			java.util.List<String> mergintifs = new ArrayList<String>();
			int iscolortif = splitTIFF2(tif1 ,"" , splitpath , mergintifs);
			includecolortif = (iscolortif == 1) ;
			mergineTIFF2(mergintifs,merginfile,includecolortif);
			DeleteFolder(splitpath);
			//
			return merginfile ;
		}catch(Exception ex){
			ex.printStackTrace();
			return tif ;
		} 
	}
	
	/*
	0黑白tif 1彩色tif
	*/
	public static int splitTIFF2(String tif , String flag , String splitpath , java.util.List<String> mergintifs){
		int result = 0 ; 
		boolean isdebug = false;
		int width  = 1700 ;
		int height = 2300 ;
		try { 
			SeekableStream seekableStream = new FileSeekableStream(new File(tif));  
	        TIFFDecodeParam param = new TIFFDecodeParam();  
	        ImageDecoder imageDecoder = ImageCodec.createImageDecoder("tiff", seekableStream, param);  
	        int numPages = imageDecoder.getNumPages();  
	        RenderedImage outImage = null;
	        ImageEncoder imageEncoder = null;
	        OutputStream outStr;
	        TIFFEncodeParam encodedParameters = new TIFFEncodeParam(); 
	        for (int i = 0; i < numPages; i++) {
	            outImage = imageDecoder.decodeAsRenderedImage(i);
				String disfile = splitpath +  flag + "_" + i + ".tiff";
	            outStr = new FileOutputStream(disfile);
	            TIFFDirectory dir =	(TIFFDirectory)outImage.getProperty("tiff_directory");
				//
				if(i == 0){
					int compressionType = TIFFEncodeParam.COMPRESSION_NONE;
					TIFFField field = dir.getField( 259 ); // compression ermitteln  
					if ( field != null ) {
						compressionType = field.getAsInt(0);
					}
					if(compressionType == TIFFEncodeParam.COMPRESSION_LZW)
						result = 1;
				}
				//
				try{
		            //转换成bmp格式
	            	long s = System.currentTimeMillis();
		            ByteArrayOutputStream out = new ByteArrayOutputStream();
					BMPEncodeParam bmpparam = new BMPEncodeParam(); 
		            ImageEncoder enc = ImageCodec.createImageEncoder("BMP", out , new BMPEncodeParam());
			        enc.encode(outImage); 
					if(isdebug){
						System.out.println("1 = " + (System.currentTimeMillis() - s) );
						s = System.currentTimeMillis();
					}
			        //固定bmp大小
			        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			        BufferedImage img = ImageIO.read(in); 
		            int imageType = img.getType();
		            int w = img.getWidth();  
		            int h = img.getHeight(); 
		            float sfbl = 1l; 
		            if(w > width || h > height){
			            BigDecimal   b  =   new  BigDecimal(1 - (float)(w-width)/w);  
			            float   sfblw   =  b.setScale(2,  BigDecimal.ROUND_HALF_UP).floatValue();  
			            b  =   new  BigDecimal(1 - (float)(h-height) / h);  
			            float   sfblh   =  b.setScale(2,  BigDecimal.ROUND_HALF_UP).floatValue();  
			            sfbl = sfblw < sfblh ? sfblw : sfblh ;
		            } 
		            BufferedImage imgOut = new BufferedImage(width, height, imageType ); 
					//
		            Graphics2D dg = (Graphics2D) imgOut.createGraphics(); 
		            dg.setBackground(Color.WHITE);
		            dg.fillRect(0,0,width ,height);
		            AffineTransform at = new AffineTransform();
		            at.scale(sfbl, sfbl); 
		            BufferedImageOp bi = new AffineTransformOp(at, null); 
		            if(sfbl < 1 ){
		            	int rw = (int)( w*sfbl) ;
		            	int rh = (int)(h*sfbl );
		            	dg.drawImage(img, bi, (width-rw) / 2 , (height-rh) / 2 );
		            }else
		            	dg.drawImage(img, bi, (width-w) / 2 , (height-h) / 2 ); 
		            dg.dispose();
					//
					if(result == 0){
						int[] rgbArray2 = new int[ height * width + width ]; 
						imgOut.getRGB(0, 0, width, height, rgbArray2, 0, width);
						for(int j = 0 ; j < rgbArray2.length ; j++){ 
							if(rgbArray2[j] == -1 || rgbArray2[j] == 0 )
								rgbArray2[j] = -16777216;
							else 
								rgbArray2[j] = -1; 
						} 
						imgOut.setRGB(0, 0, width, height , rgbArray2, 0, width);
					}
					//
		            out.close();
		            in.close(); 
					if(isdebug){
						System.out.println("2 = " + (System.currentTimeMillis() - s) );
						s=System.currentTimeMillis();
					}
		            //转换成tif格式
		            OutputStream os = new FileOutputStream(disfile); 
		            TIFFEncodeParam tifparam = new TIFFEncodeParam();
					if(result == 0){
						tifparam.setCompression(TIFFEncodeParam.COMPRESSION_GROUP4); 
					}
					TIFFField[] extras = new TIFFField[2]; 
            		extras[0] = new TIFFField(282, TIFFTag.TIFF_RATIONAL, 1, (Object) new long[][]{{(long) 200, 1}, {0, 0}});
            		extras[1] = new TIFFField(283, TIFFTag.TIFF_RATIONAL, 1, (Object) new long[][]{{(long) 200, 1}, {0, 0}});
	        		tifparam.setExtraFields(extras);
			        ImageEncoder enc1 = ImageCodec.createImageEncoder("TIFF", os, tifparam);  
			        enc1.encode(imgOut); 
			        os.close();
					if(isdebug){
						System.out.println("3 = " + (System.currentTimeMillis() - s) );
					}
	            }catch (Exception e) { 
	            	BufferedOutputStream bOutStr = new BufferedOutputStream(new FileOutputStream(disfile));
					if(result == 0)	encodedParameters.setCompression(TIFFEncodeParam.COMPRESSION_GROUP4);
	            	ImageEncoder imageEncoder1 = ImageCodec.createImageEncoder("tiff", bOutStr, encodedParameters);
		            imageEncoder1.encode(outImage);
		            bOutStr.flush();
		            bOutStr.close();
	    	    }
				//
				mergintifs.add(disfile);
	        }
		} catch (Exception e) {   
	        e.printStackTrace();   
	    } 
		return result;
	}
	
	public static void mergineTIFF2(java.util.List<String> tifs , String merginefile,boolean includecolortif){
		try
		{
			File[] files = new File[tifs.size()];
			for(int i = 0 ; i < tifs.size() ; i++){
				files[i] = new File((String)tifs.get(i));
			} 
			ArrayList pages = new ArrayList(files.length - 1);
			RenderedOp firstPage =	JAI.create("fileload", files[0].getCanonicalPath());
			for (int i = 1; i < files.length; i++)
			{
				RenderedOp page = JAI.create("fileload", files[i].getCanonicalPath());
				pages.add(page);
			}
			TIFFEncodeParam param = new TIFFEncodeParam();
			//param.setCompression(TIFFEncodeParam.COMPRESSION_PACKBITS);
			if(!includecolortif)
				param.setCompression(TIFFEncodeParam.COMPRESSION_GROUP4);
			TIFFField[] extras = new TIFFField[2]; 
            extras[0] = new TIFFField(282, TIFFTag.TIFF_RATIONAL, 1, (Object) new long[][]{{(long) 200, 1}, {0, 0}});
            extras[1] = new TIFFField(283, TIFFTag.TIFF_RATIONAL, 1, (Object) new long[][]{{(long) 200, 1}, {0, 0}});
	        param.setExtraFields(extras);
			//合并后生成的图片
			OutputStream out = new FileOutputStream(merginefile);
			ImageEncoder encoder =
			ImageCodec.createImageEncoder("TIFF", out, param);
			param.setExtraImages(pages.iterator());
			encoder.encode(firstPage);
			firstPage.dispose();
			for (int i = 0; i < pages.size(); i++)
			{
				((RenderedOp)pages.get(i)).dispose();
			}
			out.close();
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}
}
