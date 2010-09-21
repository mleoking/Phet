/* ------------------------------------------------------------ */
/*
HTTrack Website Copier, Offline Browser for Windows and Unix
Copyright (C) Xavier Roche and other contributors

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.


Important notes:

- We hereby ask people using this source NOT to use it in purpose of grabbing
emails addresses, or collecting any other private information on persons.
This would disgrace our work, and spoil the many hours we spent on it.


Please visit our Website: http://www.httrack.com
*/

/* ------------------------------------------------------------ */
/* File: httrack.c subroutines:                                 */
/*       wizard system (accept/refuse links)                    */
/* Author: Xavier Roche                                         */
/* ------------------------------------------------------------ */


#ifndef HTSWIZARD_DEFH
#define HTSWIZARD_DEFH 

/* Library internal definictions */
#ifdef HTS_INTERNAL_BYTECODE

#include "htsglobal.h"

/* Forward definitions */
#ifndef HTS_DEF_FWSTRUCT_httrackp
#define HTS_DEF_FWSTRUCT_httrackp
typedef struct httrackp httrackp;
#endif
#ifndef HTS_DEF_FWSTRUCT_lien_url
#define HTS_DEF_FWSTRUCT_lien_url
typedef struct lien_url lien_url;
#endif

int hts_acceptlink(httrackp* opt,
                   int ptr,int lien_tot,lien_url** liens,
                   char* adr,char* fil,
                   char* tag, char* attribute,
                   int* set_prio_to_0,
                   int* just_test_it);
int hts_testlinksize(httrackp* opt,
                     char* adr,char* fil,
                     LLint size);
int hts_acceptmime(httrackp* opt,
                   int ptr,int lien_tot,lien_url** liens,
                   char* adr,char* fil,
                   char* mime);
#endif

#endif
