import React from 'react';
import { Clock, Calendar, DollarSign, Star, ArrowRight } from 'lucide-react';
import { HeartIcon } from '@heroicons/react/24/solid';

const GigListItem = ({ 
  gig, 
  myUser, 
  onGigClick, 
  onSave, 
  onApplyClick,
  isUrgent,
  isLate,
  formatPostedTime,
  getAvatarInitials,
  refreshstate,
  setRefreshstate,
  setShowLoginRequired
}) => {
  return (
    <div 
      className="bg-white rounded-xl border border-gray-200 hover:shadow-lg transition-all duration-300 cursor-pointer group p-6"
      onClick={() => onGigClick(gig)}
    >
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <div className="flex items-center space-x-2 mb-2">
            <span className="text-sm text-gray-500 bg-gray-100 px-2 py-1 rounded-full">
              {gig.category}
            </span>
           {(() => {
              if (!isUrgent && !isLate) return null;

              return (
                <span className="bg-red-100 text-red-600 px-2 py-1 rounded-full text-xs font-medium">
                  {isUrgent ? "Urgent" : "Late"}
                </span>
              );
            })()}

            {gig.poster.averageRating > 4.5 && (
              <div className="w-4 h-4 bg-blue-500 rounded-full flex items-center justify-center">
                <div className="w-2 h-2 bg-white rounded-full"></div>
              </div>
            )}
          </div>

          <h3 className="text-xl font-bold text-gray-800 mb-2 group-hover:text-blue-600 transition-colors">
            {gig.title}
          </h3>
          
          <p className="text-gray-600 mb-3 line-clamp-2">
            {gig.description}
          </p>

          <div className="flex items-center space-x-6 text-sm text-gray-500">
            <div className="flex items-center space-x-1">
              <Clock className="h-4 w-4" />
              <span>{gig.employmentType}</span>
            </div>
            <div className="flex items-center space-x-1">
              <Calendar className="h-4 w-4" />
              <span>{formatPostedTime(gig.postedTime)}</span>
            </div>
            <div className="flex items-center space-x-1">
              <DollarSign className="h-4 w-4" />
              <span>/ {gig.salaryFrequency}</span>
            </div>
          </div>
        </div>

        <div className="flex flex-col items-end space-y-3 ml-6">
          { (gig.poster.id != myUser?.id) ? (
            <button 
              className={`text-gray-400 hover:text-red-500 transition-colors ${(gig.isSavedByCurrentUser) ? 'text-red-500' : ''}`}
              onClick={ async (e) => {
                e.stopPropagation();
                if (myUser){
                  var saved = await onSave(gig.id, gig.isSavedByCurrentUser);
                  gig.isSavedByCurrentUser = saved;
                  setRefreshstate(!refreshstate);
                }
                else {
                  setShowLoginRequired(true)
                }
              }}
            >
              <HeartIcon className="h-5 w-5" />
            </button>
          ): null}
          
          <div className="text-right">
            <div className="text-3xl font-bold text-green-600">
              {gig.salaryRange ? gig.salaryRange : `$${gig.salary}`}
            </div>
            <div className="text-xs text-gray-500">{gig.salaryCurrency} / {gig.salaryFrequency}</div>
          </div>

          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 bg-gradient-to-r from-blue-600 to-slate-600 rounded-full flex items-center justify-center">
              <span className="text-white font-bold text-xs">{getAvatarInitials(gig.poster.nickName)}</span>
            </div>
            <div>
              <div className="text-sm font-medium text-gray-800">{gig.poster.nickName}</div>
              {gig.poster.averageRating && (
                <div className="flex items-center space-x-1">
                  <Star className="h-3 w-3 text-yellow-500 fill-current" />
                  <span className="text-xs text-gray-500">{gig.poster.averageRating}</span>
                </div>
              )}
            </div>
          </div>

          <button 
            className="bg-gradient-to-r from-blue-600 to-slate-600 text-white py-2 px-6 rounded-lg font-medium hover:shadow-lg transform hover:-translate-y-0.5 transition-all flex items-center space-x-2"
            onClick={(e) => {
              e.stopPropagation();
              if (myUser){
                onApplyClick(gig);
              }
              else{
                setShowLoginRequired(true);                    
              }
            }}
          >
            <span>Apply</span>
            <ArrowRight className="h-4 w-4" />
          </button>
        </div>
      </div>
    </div>
  );
};

export default GigListItem;