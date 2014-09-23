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
	 * 鑾峰彇tif鏂囦�?
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
			String localfile = localpath +  mFileName ;//涓嬭浇鍚庣殑鏈湴鏂囦欢鍚�
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
	 * 杞崲tif鏂囦�?
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
		            //杞崲鎴恇mp鏍煎�?
	            	long s = System.currentTimeMillis();
		            ByteArrayOutputStream out = new ByteArrayOutputStream();
					BMPEncodeParam bmpparam = new BMPEncodeParam();					 
		            ImageEncoder enc = ImageCodec.createImageEncoder("BMP", out , new BMPEncodeParam());
			        enc.encode(outImage); 
					if(isdebug){
						System.out.println("1 = " + (System.currentTimeMillis() - s) );
						s = System.currentTimeMillis();
					}
			        //鍥哄畾bmp澶у皬
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
		            //杞崲鎴恡if鏍煎�?
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
			//鍚堝苟鍚庣敓鎴愮殑鍥剧墖
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
	String R_merginfile = "/worktmp/"+adminID + "/ps.tif"; //鍚堝苟tif鏂囦欢鐨勭浉瀵规枃浠跺悕
	String czh="";//浜ц瘉鍙�
	DeleteFolder(ftptifpath);
	DeleteFolder(splitpath); 
	new File(ftptifpath).mkdirs();
	new File(splitpath ).mkdirs();
	String path1 = "";
	String[] fileIds = fileId.split(",");
	
	//���⵵��6.D4.7.1999-015445���ǩ��ʱ����
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
	
	//�������ǩ��չʾ���ȵ�ʵ��
	LySignpackagenum signnum = new LySignpackagenum();
	
	for(int i=0;i<fileIds.length;i++){
		try {
			//�ѵ�ǰ��ȡ��i��ǩ�´���nums,������ǩ�¼�¼����total������һֱ�޸Ĵ�������
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
	String czh="";//浜ц瘉鍙�
	
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
		//ǩ����ʱ����С,��������ĵ��ŵ�tiff�ļ����մ��ǩ�µ����̴���
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
		//Ԥ������ĿĿ¼��worktmp���뵥��ǩ�������⵵�ŵ�tiff�ļ����ӿ���ٶ�
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
	//ǩ����ʱ����С,�Դ˵��ŵ�tiff�ļ����մ��ǩ�µ����̴���
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
    // 鍒ゆ柇鐩綍鎴栨枃浠舵槸鍚﹀瓨鍦�? 
    if (!file.exists()) {  // 涓嶅瓨鍦ㄨ繑鍥�false   
        return flag;   
    } else {   
        // 鍒ゆ柇鏄惁涓烘枃浠�? 
        if (file.isFile()) {  // 涓烘枃浠舵椂璋冪敤鍒犻櫎鏂囦欢鏂规硶   
            return deleteFile(sPath);   
        } else {  // 涓虹洰褰曟椂璋冪敤鍒犻櫎鐩綍鏂规硶   
            return deleteDirectory(sPath);   
        }   
    }   
}

/**  
 * 鍒犻櫎鍗曚釜鏂囦�? 
 * @param   sPath    琚垹闄ゆ枃浠剁殑鏂囦欢鍚� 
 * @return 鍗曚釜鏂囦欢鍒犻櫎鎴愬姛杩斿洖true锛屽惁鍒欒繑鍥�?alse  
 */  
public static boolean deleteFile(String sPath) {   
	boolean flag = false;   
	File file = new File(sPath);   
    // 璺緞涓烘枃浠朵笖涓嶄负绌哄垯杩涜鍒犻�?  
    if (file.isFile() && file.exists()) {   
        flag = file.delete();   
        flag = true;   
    }   
    return flag;   
}

/**  
 * 鍒犻櫎鐩綍锛堟枃浠跺す锛変互鍙婄洰褰曚笅鐨勬枃浠� 
 * @param   sPath 琚垹闄ょ洰褰曠殑鏂囦欢璺�? 
 * @return  鐩綍鍒犻櫎鎴愬姛杩斿洖true锛屽惁鍒欒繑鍥�?alse  
 */  
public static boolean deleteDirectory(String sPath) {   
    //濡傛灉sPath涓嶄互鏂囦欢鍒嗛殧绗︾粨灏撅紝鑷姩娣诲姞鏂囦欢鍒嗛殧绗�? 
    if (!sPath.endsWith(File.separator)) {   
        sPath = sPath + File.separator;   
    }   
    File dirFile = new File(sPath);   
    //濡傛灉dir瀵瑰簲鐨勬枃浠朵笉�?樺湪锛屾垨鑰呬笉鏄竴涓洰褰曪紝鍒欓�鍑�  
    if (!dirFile.exists() || !dirFile.isDirectory()) {   
        return false;   
    }   
    boolean flag = true;   
    //鍒犻櫎鏂囦欢澶�?笅鐨勬墍鏈夋枃浠�鍖呮嫭瀛愮洰褰�?  
    File[] files = dirFile.listFiles();   
    for (int i = 0; i < files.length; i++) {   
        //鍒犻櫎�?愭枃浠�  
        if (files[i].isFile()) {   
            flag = deleteFile(files[i].getAbsolutePath());   
            if (!flag) break;   
        } //鍒犻櫎�?愮洰褰�  
        else {   
            flag = deleteDirectory(files[i].getAbsolutePath());   
            if (!flag) break;   
        }   
    }   
    if (!flag) return false;   
    //鍒犻櫎褰撳墠鐩�?  
    if (dirFile.delete()) {   
        return true;   
    } else {   
        return false;   
    }   
}
/**
 * 签章查询
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
			if(!Tool.isEmpty(paras.get("xlh")))//序列�?
			{
				obj.add("%"+paras.get("xlh").trim()+"%");
				sb.append(" and t.qzxlh like ?");
			}
			 if(!Tool.isEmpty(paras.get("dh")))//档号
			{
				obj.add("%"+paras.get("dh").trim()+"%");
				sb.append(" and t.acode like ?");
			}
			 if(!Tool.isEmpty(paras.get("qzuser")))//签章�?
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
	 * ȡ��ǰ���ǩ�����ڶ�ȡ����ֵ
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
	    	boolean includecolortif = false ; //�Ƿ������ɫtif 
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
	0�ڰ�tif 1��ɫtif
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
		            //ת����bmp��ʽ
	            	long s = System.currentTimeMillis();
		            ByteArrayOutputStream out = new ByteArrayOutputStream();
					BMPEncodeParam bmpparam = new BMPEncodeParam(); 
		            ImageEncoder enc = ImageCodec.createImageEncoder("BMP", out , new BMPEncodeParam());
			        enc.encode(outImage); 
					if(isdebug){
						System.out.println("1 = " + (System.currentTimeMillis() - s) );
						s = System.currentTimeMillis();
					}
			        //�̶�bmp��С
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
		            //ת����tif��ʽ
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
			//�ϲ������ɵ�ͼƬ
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
