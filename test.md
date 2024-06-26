# 주문 전송 API 명세서



NGP자사몰의 주문확정내역을 KT&G에 전송하는 API

전송시간은 확정된 데이타를 기준으로 1일 1회 특정시간에 일괄 전송한다.



※ 주문확정내역 : 주문상태가 '결제완료' 인 데이타 (결제완료 ? or 배송시작 ?)

※ 전송시간     : 상호협의가 필요함(???)



※ 전송방향 : NGP -> KTG ( NGP 에서 KT&G 에서 제공한 URL 을 호출하여 전송 )

※ 전송주기 : 1일 1회



## 주문전송(NGP -> KTG) 호출 URL



* 개발 : http://webmiseev.ktng.com/ktng/eai/srrngmsal001.json

* STG  : http://webmisstg.ktng.com/ktng/eai/srrngmsal001.json

* 운영 : http://webmis.ktng.com/ktng/eai/srrngmsal001.json



※ URL 예시이며 달라질 EAI 작업으로 달라질 수 있음!!!

※ URL 확정시 버전을 업데이트 하여 제공예정입니다.



## 주문전송(NGP -> KTG) 호출 JSON 예시



curl -X POST 'http://webmiseev.ktng.com/ktng/eai/srrngmsal001.json' \

-H 'Content-Type: application/json' \

-d '{

    "HEADER": {

        "IF_ID": "SRR_NGMSAL_001",

        "IF_TRC_ID": "2A56764C-450B-1EEF-8B8F-681B9F7D3343",

        "ADDITIONAL_INFO": "",

        "RST_CD": "",

        "RST_MSG": ""

    },

    "BODY": {

        "IR_SRATZUDE_NGP_INT": [

            {

                "REQ_DATE": "20240626",

                "REQ_TIME": "131112",

                "NGP_NO": "2024062600000002",

                "PROD_CODE": "10002246A1",

                "REQ_QTY": 2,

                "NOR_UNIT": 50000,

                "NOR_AMT": 100000,

                "DIS_AMT": 30000,

                "REQ_AMT": 70000,

                "SUP_AMT": 63637,

                "TAX_AMT": 6363,

                "PSN_NAME": "홍길동",

                "RPS_HP_TEL": "01012345678",

                "PSN_POST": "12345",

                "PSN_ADDR1": "대전 서구 둔산로123번길 15",

                "PSN_ADDR2": "웅진빌딩 701호 KT&G ITO 센터",

                "DELIVERY_REMARK": "우편함에 넣어주세요",

                "LINE_CNT": 2,

                "TIMESTAMP": "20240626110011",

                "IUD_FLAG": "I",

                "ATTRIBUTE1": "",

                "ATTRIBUTE2": "",

                "ATTRIBUTE3": "",

                "ATTRIBUTE4": "",

                "ATTRIBUTE5": ""

            },

            {

                "REQ_DATE": "20240626",

                "REQ_TIME": "131112",

                "NGP_NO": "2024062600000002",

                "PROD_CODE": "10002343A0",

                "REQ_QTY": 1,

                "NOR_UNIT": 77000,

                "NOR_AMT": 77000,

                "DIS_AMT": 0,

                "REQ_AMT": 77000,

                "SUP_AMT": 70000,

                "TAX_AMT": 7000,

                "PSN_NAME": "홍길동",

                "RPS_HP_TEL": "01012345678",

                "PSN_POST": "12345",

                "PSN_ADDR1": "대전 서구 둔산로123번길 15",

                "PSN_ADDR2": "웅진빌딩 701호 KT&G ITO 센터",

                "DELIVERY_REMARK": "우편함에 넣어주세요",

                "LINE_CNT": 2,

                "TIMESTAMP": "20240626110011",

                "IUD_FLAG": "I",

                "ATTRIBUTE1": "",

                "ATTRIBUTE2": "",

                "ATTRIBUTE3": "",

                "ATTRIBUTE4": "",

                "ATTRIBUTE5": ""

            }

        ]

    }

}'





##  주문전송(NGP -> KTG) 호출 결과 JSON 예시



1) 성공시: RST_CD = "0000"



{

    "HEADER": {

        "IF_ID": "SRR_NGMSAL_001",

        "IF_TRC_ID": "2A56764C-450B-1EEF-8B8F-681B9F7D48B7",

        "ADDITIONAL_INFO": "",

        "RST_CD": "0000",

        "RST_MSG": "SUCCESS"

    }

}





2) 실패시: RST_CD != "0000"



{

    "HEADER": {

        "IF_ID": "SRR_NGMSAL_001",

        "IF_TRC_ID": "2A56764C-450B-1EEF-8B8F-681B9F7D48B7",

        "ADDITIONAL_INFO": "",

        "RST_CD": "9999",

        "RST_MSG": "존재하지 않는 IF_ID 입니다."

    }

}



## 요청 JSON 주요항목설명



{

    "HEADER": {                                              // 고정값 : HEADER 시작을 표시

        "IF_ID": "SRR_NGMSAL_001",                           // 고정값 : SRR_NGMSAL_001

        "IF_TRC_ID": "2A56764C-450B-1EEF-8B8F-681B9F7D3343", // UUID   : API 호출 추적용 값이며, 호출시 셋팅

        "ADDITIONAL_INFO": "",

        "RST_CD": "",

        "RST_MSG": ""

    },

    "BODY": {                                                // 고정값: BODY 시작을 표시

        "IR_SRATZUDE_NGP_INT": [                             // 고정값: IR_SRATZUDE_NGP_INT : 주문배열 시작

            {

                "REQ_DATE": "20240626",                      // (필수:문자) 주문일자

                "REQ_TIME": "131112",                        // (필수:문자) 주문시간

                "NGP_NO": "2024062600000002",                // (필수:문자) 주문번호(NGP)

                "PROD_CODE": "10002246A1",                   // (필수:문자) 품목코드(KTG): KT&G의 품목코드로 매핑하여 전송

                "REQ_QTY": 2,                                // (필수:숫자) 주문수량

                "NOR_UNIT": 50000,                           // (필수:숫자) 정상단가

                "NOR_AMT": 100000,                           // (필수:숫자) 정상금액

                "DIS_AMT": 30000,                            // (필수:숫자) 할인금액

                "REQ_AMT": 70000,                            // (필수:숫자) 합계금액 = (정상금액 - 할인금액) = (공급가 + 부가세)

                "SUP_AMT": 63637,                            // (필수:숫자) 공급가

                "TAX_AMT": 6363,                             // (필수:숫자) 부가세

                "PSN_NAME": "홍길동",                        // (필수:문자) (배송정보)고객명

                "RPS_HP_TEL": "01012345678",                 // (필수:문자) (배송정보)고객전화번호

                "PSN_POST": "12345",                         // (필수:문자) (배송정보)우편번호

                "PSN_ADDR1": "대전 서구 둔산로123번길 15",   // (필수:문자) (배송정보)주소

                "PSN_ADDR2": "웅진빌딩 701호 KT&G ITO 센터", // (필수:문자) (배송정보)상세주소

                "DELIVERY_REMARK": "우편함에 넣어주세요",    // (옵션:문자) (배송정보)배송시요청사항

                "LINE_CNT": 2,                               // (필수:숫자) 주문번호별 라인수 (ex: 주문상품이 2건이라면 두건 모두 2로 표기)

                "TIMESTAMP": "20240626150011",               // (필수:문자) 변경일시: 전송시 일시 YYYYMMDDHH24MISS

                "IUD_FLAG": "I",                             // (필수:문자) 변경FLAG: I=신규 / D=삭제 / U는 없음

                "ATTRIBUTE1": "",                            // (옵션:문자) 업무변경대비 여유항목

                "ATTRIBUTE2": "",                            // (옵션:문자) 업무변경대비 여유항목

                "ATTRIBUTE3": "",                            // (옵션:문자) 업무변경대비 여유항목

                "ATTRIBUTE4": "",                            // (옵션:문자) 업무변경대비 여유항목

                "ATTRIBUTE5": ""                             // (옵션:문자) 업무변경대비 여유항목

            },

            {                                                // 두번째 배열 시작

                "REQ_DATE": "20240626",

                "REQ_TIME": "131112",

                "NGP_NO": "2024062600000002",

                "PROD_CODE": "10002343A0",

                "REQ_QTY": 1,

                "NOR_UNIT": 77000,

                "NOR_AMT": 77000,

                "DIS_AMT": 0,

                "REQ_AMT": 77000,

                "SUP_AMT": 70000,

                "TAX_AMT": 7000,

                "PSN_NAME": "홍길동",

                "RPS_HP_TEL": "01012345678",

                "PSN_POST": "12345",

                "PSN_ADDR1": "대전 서구 둔산로123번길 15",

                "PSN_ADDR2": "웅진빌딩 701호 KT&G ITO 센터",

                "DELIVERY_REMARK": "우편함에 넣어주세요",

                "LINE_CNT": 2,

                "TIMESTAMP": "20240626150011",

                "IUD_FLAG": "I",

                "ATTRIBUTE1": "",

                "ATTRIBUTE2": "",

                "ATTRIBUTE3": "",

                "ATTRIBUTE4": "",

                "ATTRIBUTE5": ""

            }

        ]

    }

}



## 리턴 JSON 주요항목설명

{

    "HEADER": {

        "IF_ID": "SRR_NGMSAL_001",                            // 고정값 : SRR_NGMSAL_001

        "IF_TRC_ID": "2A56764C-450B-1EEF-8B8F-681B9F7D48B7",  // UUID   : 요청시 셋팅했던 값

        "ADDITIONAL_INFO": "",

        "RST_CD": "9999",                                     // 성공: 0000 / 실패: 9999

        "RST_MSG": "존재하지 않는 IF_ID 입니다."              // 설명메시지

    }

}
