
#ifndef  __tmpAecPublic_h__
#define  __tmpAecPublic_h__

//
#include    "audioProcPublic.h"


//
//
typedef  struct  __aecLayout_t				{
    unsigned  int				tn_modified;
    //
    int						iTaskId;
    unsigned  short			usCntLimit_activeMems_from;
    //
    bool                    bUse_audioMixer;

    //
    unsigned  short			usCnt;
    //AEC_player_cfg				mems[CONST_cntof_aecPlayers];

    //
    pthread_t               tid_mix;

    //
    pthread_t               tid_aec;

    //
    bool                    bQuit;

    //
    TmpMixer                tmpMixer;

    //
    struct                  {
        long long tickCnt_startToMix;
        int nTimes_mix;

        //
        long long tickCnt_lastMixed;
        int nTimes_mix_err;

        //
        long long tickCnt_last_adjustQNodes_mix;      //  当队列的节点数太大时，需要加快一下混音，以便让声音及时播放
    }                       mixInfo;

    //
    TmpAp                   tmpAp;

    //
    struct                  {
        long long tickCnt_startToAp;
        int nTimes_ap;

    }                       apInfo;


    //
    void            *       pAecObj;
    //
    void            *       pNsObj;

    //
    bool                    bDbg1;

    //
    struct                  {
        //
        int                 nStep_aec;
        int                 nStep_mix;

        //
        int                 nStep_ap;

    }                       status;

    //
}		 AEC_layout;


#if 0
//
#define     M_maxQNodes_playAudioQ      50  //  25

//
#define     M_maxQNodes_aecPlayQ        50  //  25
#define     M_maxQNodes_aecQ            24  //  12
#endif
//
//#define     M_maxQNodes_playAudioQ      12  //  25
#define     M_maxQNodes_playAudioQ      16  //  25

//
#define     M_maxQNodes_aecPlayQ        16  //  25
#define     M_maxQNodes_aecQ            8  //  12



//
int AEC_layout_init(int  iTaskId,  unsigned  short  usCntLimit_activeMems_from, bool  bUse_audioMixer,  AEC_layout* pLayout);
int AEC_layout_exit(AEC_layout* pLayout);




#endif

